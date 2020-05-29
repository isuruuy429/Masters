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
from proposer_work import get_acceptors_from_service_registry
from coordinator_work import check_active_nodes, decide_roles, inform_acceptors, schedule_work_for_proposers, \
    update_service_registry

counter = Value('i', 0)
app = Flask(__name__)

port_number = int(sys.argv[1])
assert port_number

node_name = sys.argv[2]
assert node_name

node_id = generate_node_id()
bully = Bully(node_name, node_id, port_number)
service_register_status = register_service(node_name, port_number, node_id)


def init(wait=True):
    if service_register_status == 200:
        ports_of_all_nodes = get_ports_of_nodes()
        del ports_of_all_nodes[node_name]
        print("Available nodes to start the election: \n %s" % ports_of_all_nodes)
        node_details = get_details(ports_of_all_nodes)

        if wait:
            timeout = random.randint(5, 15)
            time.sleep(timeout)
            print('timeouting in %s seconds' % timeout)

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
                master_work()
            else:
                election(higher_nodes_array, node_id)
    else:
        print('Service registration is not successful')


@app.route('/nodeDetails', methods=['GET'])
def get_node_details():
    coordinator_bully = bully.coordinator
    node_id_bully = bully.node_id
    election_bully = bully.election
    node_name_bully = bully.node_name
    port_number_bully = bully.port
    return jsonify({'node_name': node_name_bully, 'node_id': node_id_bully, 'coordinator': coordinator_bully,
                    'election': election_bully, 'port': port_number_bully}), 200


@app.route('/response', methods=['POST'])
def response_node():
    data = request.get_json()
    incoming_node_id = data['node_id']
    self_node_id = bully.node_id
    if self_node_id > incoming_node_id:
        threading.Thread(target=init, args=[False]).start()
        bully.election = False
    return jsonify({'Response': 'OK'}), 200


@app.route('/announce', methods=['POST'])
def announce_coordinator():
    data = request.get_json()
    coordinator = data['coordinator']
    bully.coordinator = coordinator
    print('Coordinator is %s ' % coordinator)
    return jsonify({'response': 'OK'}), 200


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


def master_work():
    active_nodes_array = check_active_nodes(node_name)
    roles = decide_roles(active_nodes_array)
    combined = inform_acceptors(roles, node_name)
    schedule_work_for_proposers(combined)
    print('roles', roles)
    update_service_registry(combined)
    proposer_count = 0
    for each in roles:
        if roles[each] == 'Proposer':
            proposer_count = proposer_count + 1
    print('proposer_count', proposer_count)
    proposer_count_data = {"proposer_count": proposer_count}

    for each in combined:
        if combined[each][0] == 'Learner':
            url = 'http://localhost:%s/learner' % combined[each][1]
            print(url)
            requests.post(url, json=proposer_count_data)


@app.route('/acceptor', methods=['POST'])
def acceptors():
    data = request.get_json()
    print(data)
    return jsonify({'response': 'OK'}), 200


@app.route('/learner', methods=['POST'])
def learners():
    data = request.get_json()
    print(data)
    return jsonify({'response': 'OK'}), 200


@app.route('/proposer', methods=['POST'])
def proposers():
    check_coordinator_health()
    data = request.get_json()
    print(data)
    return jsonify({'response': 'OK'}), 200


@app.route('/primeResult', methods=['POST'])
def prime_result():
    data = request.get_json()
    print('prime result from proposer', data)
    return jsonify({'response': 'OK'}), 200


@app.route('/proposer-schedule', methods=['POST'])
def proposer_schedule():
    data = request.get_json()
    print(data)
    start = data['start']
    end = data['end']
    random_number = data['random_number']

    print('Checking %s number for prime....' % random_number)
    if random_number <= 1:
        return f'{random_number} number'
    else:
        print('starting dividing from %s ' % start)
        for number in range(start, end):
            print('now dividing from %s' % number)
            if random_number % number == 0 and random_number != number:
                print(f"{random_number} is divisible by {number}. {random_number} is not a prime number")
                result_string = f"{random_number} is divisible by {number}. {random_number} is not a prime number"
        print(f"{random_number} is a prime number")
        result_string = f"{random_number} is a prime number"

    data = {"primeResult": result_string}
    print(data)
    # url_acceptor = get_acceptors_from_service_registry()
    # print(url_acceptor)
    # print('Sending the result to a random acceptor %s' % url_acceptor)
    # requests.post(url_acceptor, json=data)

    try:
        acceptor_array = {}
        response = requests.get('http://127.0.0.1:8500/v1/agent/services')
        nodes = response.json()

        for each in nodes:
            if nodes[each]['Meta']['Role'] == 'Acceptor':
                node = nodes[each]['Service']
                role = nodes[each]['Port']
                key = node
                value = role
                acceptor_array[key] = value
    except:
        pass
    finally:
        random_acceptor = random.choice(list(acceptor_array.items()))
        url = 'http://localhost:%s/primeResult' % random_acceptor[1]
        print(url)


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
