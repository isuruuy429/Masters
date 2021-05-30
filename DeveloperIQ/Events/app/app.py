from flask import Flask, jsonify
import boto3
import os

app = Flask(__name__)

aws_access_key_id = os.environ.get('AWS_ACCESS_KEY_ID')
aws_secret_access_key = os.environ.get('AWS_SECRET_ACCESS_KEY')
HOST = '0.0.0.0'
PORT = 5002
region = 'us-east-2'

dynamodb = boto3.resource('dynamodb', region, aws_access_key_id=aws_access_key_id,
                          aws_secret_access_key=aws_secret_access_key)


@app.route('/events', methods=['GET'])
def get_events():
    table = dynamodb.Table('events')
    response = table.scan()
    count = response['Count']
    return jsonify(count), 200


if __name__ == '__main__':
    app.run(HOST, PORT)
