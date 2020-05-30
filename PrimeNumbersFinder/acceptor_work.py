import requests
import random


def get_learner_from_service_registry():
    learner_array = {}
    response = requests.get('http://127.0.0.1:8500/v1/agent/services')
    nodes = response.json()

    for each in nodes:
        if len(nodes[each]['Meta']) > 0:
            if nodes[each]['Meta']['Role'] == 'Learner':
                node = nodes[each]['Service']
                role = nodes[each]['Port']
                key = node
                value = role
                learner_array[key] = value
    print('learner_array', learner_array)
    for each in learner_array:
        url = 'http://localhost:%s/sendToLearner' % learner_array[each]
        print(url)



#get_learner_from_service_registry()