import json
from flask import Flask, jsonify
import boto3

app = Flask(__name__)

region = 'us-east-2'
dynamodb = boto3.resource('dynamodb', region, aws_access_key_id=aws_access_key_id,
                          aws_secret_access_key=aws_secret_access_key)


@app.route('/commits', methods=['GET'])
def get_commits():
    table = dynamodb.Table('commits')
    response = table.scan()
    count = response['Count']
    return jsonify(count), 200


@app.route('/events', methods=['GET'])
def get_events():
    table = dynamodb.Table('events')
    response = table.scan()
    count = response['Count']
    return jsonify(count), 200


@app.route('/pulls', methods=['GET'])
def get_pull_requests():
    table = dynamodb.Table('pulls')
    response = table.scan()
    count = response['Count']
    return jsonify(count), 200


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
