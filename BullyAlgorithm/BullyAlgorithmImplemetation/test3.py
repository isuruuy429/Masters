from flask import Flask, request, jsonify
from util_methods import register_service, get_ports_of_nodes, generate_node_id, get_higher_nodes, election, \
    announce, ready_for_election, get_details, check_health_of_the_service
from bully import Bully
import threading
import time
import random
import sys
import requests
from multiprocessing import Value
import logging

counter = Value('i', 0)
app = Flask(__name__)

# verifying if port number and node name have been entered as command line arguments.
port_number = int(sys.argv[1])
assert port_number

node_name = sys.argv[2]
assert node_name

# saving the API logs to a file
logging.basicConfig(filename=f"logs/{node_name}.log", level=logging.INFO)

# an array to capture the messages that receive from acceptors
learner_result_array = []

node_id = generate_node_id()
bully = Bully(node_name, node_id, port_number)

# register service in the Service Registry
service_register_status = register_service(node_name, port_number, node_id)


def init(wait=True):
    if service_register_status == 200:
        ports_of_all_nodes = get_ports_of_nodes()
        del ports_of_all_nodes[node_name]

        # exchange node details with each node
        node_details = get_details(ports_of_all_nodes)

        if wait:
            timeout = random.randint(5, 15)
            time.sleep(timeout)
            print('timeouting in %s seconds' % timeout)

        # checks if there is an election on going
        election_ready = ready_for_election(ports_of_all_nodes, bully.election, bully.coordinator)
        if election_ready or not wait:
            print('Starting election in: %s' % node_name)
            bully.election = True
            higher_nodes_array = get_higher_nodes(node_details, node_id)
            print('higher node array', higher_nodes_array)
            if len(higher_nodes_array) == 0:
                bully.coordinator = True
                bully.election = False
                announce(node_name)
                print('Coordinator is : %s' % node_name)
                print('**********End of election**********************')
            else:
                election(higher_nodes_array, node_id)
    else:
        print('Service registration is not successful')


# this api is used to exchange details with each node
@app.route('/nodeDetails', methods=['GET'])
def get_node_details():
    coordinator_bully = bully.coordinator
    node_id_bully = bully.node_id
    election_bully = bully.election
    node_name_bully = bully.node_name
    port_number_bully = bully.port
    return jsonify({'node_name': node_name_bully, 'node_id': node_id_bully, 'coordinator': coordinator_bully,
                    'election': election_bully, 'port': port_number_bully}), 200


'''
This API checks if the incoming node ID is grater than its own ID. If it is, it executes the init method and 
sends an OK message to the sender. The execution is handed over to the current node. 
'''


@app.route('/response', methods=['POST'])
def response_node():
    data = request.get_json()
    incoming_node_id = data['node_id']
    self_node_id = bully.node_id
    if self_node_id > incoming_node_id:
        threading.Thread(target=init, args=[False]).start()
        bully.election = False
    return jsonify({'Response': 'OK'}), 200


# This API is used to announce the coordinator details.
@app.route('/announce', methods=['POST'])
def announce_coordinator():
    data = request.get_json()
    coordinator = data['coordinator']
    bully.coordinator = coordinator
    print('Coordinator is %s ' % coordinator)
    return jsonify({'response': 'OK'}), 200


'''
When nodes are sending the election message to the higher nodes, all the requests comes to this proxy. As the init
method needs to execute only once, it will forward exactly one request to the responseAPI. 
'''


@app.route('/proxy', methods=['POST'])
def proxy():
    with counter.get_lock():
        counter.value += 1
        unique_count = counter.value

    url = 'http://localhost:%s/response' % port_number
    if unique_count == 1:
        data = request.get_json()
        requests.post(url, json=data)

    return jsonify({'Response': 'OK'}), 200


# No node spends idle time, they always checks if the master node is alive in each 60 seconds.
def check_coordinator_health():
    threading.Timer(60.0, check_coordinator_health).start()
    health = check_health_of_the_service(bully.coordinator)
    if health == 'crashed':
        init()
    else:
        print('Coordinator is alive')


timer_thread1 = threading.Timer(15, init)
timer_thread1.start()

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=port_number)
