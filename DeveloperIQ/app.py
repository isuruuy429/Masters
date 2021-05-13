import json
from flask import Flask, jsonify
from create_json_files import write_files_once_a_day


app = Flask(__name__)


@app.route('/commits', methods=['GET'])
def get_commits():
    f = open('resources/commits.json', )
    data = json.load(f)
    f.close()

    return jsonify(data), 200


@app.route('/events', methods=['GET'])
def get_events():
    f = open('resources/events.json', )
    data = json.load(f)
    f.close()

    return jsonify(data), 200


@app.route('/pulls', methods=['GET'])
def get_pull_requests():
    f = open('resources/pulls.json', )
    data = json.load(f)
    f.close()

    return jsonify(data), 200


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
