import json
from flask import Flask, jsonify
import requests
import os

app = Flask(__name__)

HOST = '0.0.0.0'
PORT = 6000
commits_ip = os.environ.get('COMMITS')
events_ip = os.environ.get('EVENTS')
pulls_ip = os.environ.get('PULLS')


def get_commits_count():
    response = requests.get('http://{}:5001/commits'.format(commits_ip))
    return int(json.loads(response.text))


def get_events_count():
    response = requests.get('http://{}:5002/events'.format(events_ip))
    return int(json.loads(response.text))


def get_pulls_count():
    response = requests.get('http://{}:5003/pulls'.format(pulls_ip))
    return int(json.loads(response.text))


def get_total():
    commits = get_commits_count()
    pulls = get_pulls_count()
    events = get_events_count()
    total = commits + pulls + events
    return total


def commit_percentage():
    total = get_total()
    commits = get_commits_count()
    commit_per = float(commits) / float(total) * 100
    return str(commit_per) + "%"


def pulls_percentage():
    total = get_total()
    pulls = get_pulls_count()
    pulls_per = float(pulls) / float(total) * 100
    return str(pulls_per) + "%"


def events_percentage():
    total = get_total()
    events = get_events_count()
    events_per = float(events) / float(total) * 100
    return str(events_per) + "%"


@app.route('/productivity', methods=['GET'])
def final_percentage():
    commits = commit_percentage()
    events = events_percentage()
    pulls = pulls_percentage()
    data = {'commits': commits, 'events': events, 'pulls': pulls}
    return jsonify(data), 200


if __name__ == '__main__':
    app.run(HOST, PORT)
