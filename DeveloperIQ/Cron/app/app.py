import json
import os
from datetime import date
from io import StringIO

import boto3
import pandas as pd
import requests
from flask import Flask, jsonify

app = Flask(__name__)

aws_access_key_id = os.environ.get('AWS_ACCESS_KEY_ID')
aws_secret_access_key = os.environ.get('AWS_SECRET_ACCESS_KEY')


def write_commits_file(owner, repo):
    print(" ======= Checking commits for %s " % date.today())
    commits_url = "https://api.github.com/repos/{}/{}/commits".format(owner, repo)
    response = requests.get(commits_url)
    response_content = json.loads(response.text)

    commit_msgs = []
    for i, val in enumerate(response_content):
        element = i, val['commit']['author']['date'], val['commit']['message']
        commit_msgs.append(element)
    writes_to_s3("commits", commit_msgs, "isuruuy-commits")


def write_events_file(owner):
    print(" ======= Checking events for %s " % date.today())
    events_url = "https://api.github.com/users/{}/events/public".format(owner)
    response = requests.get(events_url)
    response_content = json.loads(response.text)

    event_msgs = []
    for i, val in enumerate(response_content):
        element = i, val['type'], val['repo']['name']
        event_msgs.append(element)
    writes_to_s3("events", event_msgs, "isuruuy-events")


def write_pulls_file(owner, repo):
    print(" ======= Checking pulls for %s " % date.today())
    pulls_url = "https://api.github.com/repos/{}/{}/pulls".format(owner, repo)
    response = requests.get(pulls_url)
    response_content = json.loads(response.text)

    pull_msgs = []
    for i, val in enumerate(response_content):
        element = i, val['created_at'], val['url']
        pull_msgs.append(element)

    writes_to_s3("pulls", pull_msgs, "isuruuy-pulls")


@app.route('/tos3', methods=['GET'])
def write_files_once_a_day():
    write_commits_file("dreamg429", "Semester3")
    write_events_file("dreamg429")
    write_pulls_file("dreamg429", "Semester3")
    data = {'result': "Successfully uploaded files to s3."}
    return jsonify(data), 200


def writes_to_s3(operation, response_dict, bucket):
    s3 = boto3.resource('s3', aws_access_key_id=aws_access_key_id, aws_secret_access_key=aws_secret_access_key)
    df = pd.DataFrame(response_dict)
    csv_buffer = StringIO()
    df.to_csv(csv_buffer, header=False, index=False)
    s3.Object(bucket, '{}.csv'.format(operation)).put(Body=csv_buffer.getvalue())
    print(csv_buffer.getvalue())


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5012)
