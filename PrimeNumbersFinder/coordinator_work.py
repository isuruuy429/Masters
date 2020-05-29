import requests
from util_methods import check_health_of_the_service, get_ports_of_nodes
import math
import random


def check_active_nodes(coordinator):
    registered_nodes = []
    response = requests.get('http://127.0.0.1:8500/v1/agent/services')
    nodes = response.json()
    for each_service in nodes:
        service = nodes[each_service]['Service']
        registered_nodes.append(service)
    registered_nodes.remove(coordinator)
    health_status = []
    for each in registered_nodes:
        if check_health_of_the_service(each) == 'passing':
            health_status.append(each)
    print('Tha active nodes are: ', health_status)
    return health_status


def decide_roles(node_array):
    roles = {}
    for i in range(2):
        node = node_array[i]
        role = 'Acceptor'
        key = node
        value = role
        roles[key] = value
    learner = node_array[2]
    roles[learner] = 'Learner'
    for i in range(3, len(node_array)):
        node = node_array[i]
        role = 'Proposer'
        key = node
        value = role
        roles[key] = value
    print('roles', roles)
    return roles


def inform_acceptors(roles, coordinator):
    ports_array = get_ports_of_nodes()
    del ports_array[coordinator]
    combined = {key: (roles[key], ports_array[key]) for key in roles}
    print('combined', combined)

    data_acceptor = {"role": "acceptor"}
    data_learner = {"role": "learner"}
    data_proposer = {"role": "proposer"}

    for each in combined:
        if combined[each][0] == 'Acceptor':
            url = 'http://localhost:%s/acceptor' % combined[each][1]
            print(url)
            requests.post(url, json=data_acceptor)
        elif combined[each][0] == 'Learner':
            url = 'http://localhost:%s/learner' % combined[each][1]
            print(url)
            requests.post(url, json=data_learner)
        else:
            url = 'http://localhost:%s/proposer' % combined[each][1]
            print(url)
            requests.post(url, json=data_proposer)
    return combined


def schedule_work_for_proposers(combined):
    count = 0
    range_array_proposers = []
    for each in combined:
        if combined[each][0] == 'Proposer':
            range_array_proposers.append(combined[each][1])
            count = count + 1
    print('range_array', range_array_proposers)

    random_number = read_number_from_file()
    proposer_list_len = len(range_array_proposers)
    number_range = math.floor(99999 / proposer_list_len)
    start = 2

    for each in range(proposer_list_len):
        divide_range = {
            "start": start,
            "end": start + number_range,
            "random_number": random_number
        }
        print(divide_range)
        url = 'http://localhost:%s/proposer-schedule' % range_array_proposers[each]
        print(url)
        requests.post(url, json=divide_range)

        start += number_range + 1


def read_number_from_file():
    file_name = "resources/PrimeNumbers.txt"
    with open(file_name, 'r') as f:
        lines = f.read().splitlines()
        random_number = int(random.choice(lines))
    return random_number


def get_node_ids(node_name):
    response = requests.get('http://127.0.0.1:8500/v1/agent/services')
    nodes = response.json()

    for each in nodes:
        if nodes[each]['Service'] == node_name:
            node_id = nodes[each]['ID']
    return node_id


def update_service_registry(roles):
    url = "http://localhost:8500/v1/agent/service/register"
    for each in roles:
        role_data = {
            "Name": each,
            "ID": get_node_ids(each),
            "Port": roles[each][1],
            "Meta": {"Role": roles[each][0]},
            "check": {
                "name": "Check Counter health %s" % roles[each][1],
                "tcp": "localhost:%s" % roles[each][1],
                "interval": "10s",
                "timeout": "1s"
            }
        }
        print(role_data)
        requests.put(url, json=role_data)
