import requests
import random


def get_acceptors_from_service_registry():
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
        return url
