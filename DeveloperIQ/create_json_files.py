import time
from datetime import date
import boto3
import pandas as pd
from io import StringIO

import requests
import json


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


def write_files_once_a_day():
    start_time = time.time()
    while True:
        write_commits_file("dreamg429", "Semester3")
        write_events_file("dreamg429")
        write_pulls_file("dreamg429", "Semester3")
        time.sleep(20 - ((time.time() - start_time) % 20))


def writes_to_s3(operation, response_dict, bucket):
    aws_access_key_id = 'AKIA4QZCVOBTR5XPO2UN'
    aws_secret_access_key = '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'

    s3 = boto3.resource('s3', aws_access_key_id=aws_access_key_id, aws_secret_access_key=aws_secret_access_key)
    df = pd.DataFrame(response_dict)
    csv_buffer = StringIO()
    df.to_csv(csv_buffer, header=False, index=False)
    s3.Object(bucket, '{}.csv'.format(operation)).put(Body=csv_buffer.getvalue())
    print(csv_buffer.getvalue())


write_files_once_a_day()

