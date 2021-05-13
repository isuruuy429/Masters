import time
from pathlib import Path
from datetime import date
import boto3

import requests
import json

from botocore.config import Config


def write_commits_file(owner, repo):
    print(" ======= Checking commits for %s " % date.today())
    commits_url = "https://api.github.com/repos/{}/{}/commits".format(owner, repo)
    response = requests.get(commits_url)
    response_content = json.loads(response.text)

    commit_msgs = {}
    for i, val in enumerate(response_content):
        commit_msgs[(val['commit']['author']['date'])] = (val['commit']['message'])
    print(commit_msgs)
    writes_to_s3("commits", commit_msgs)


def write_events_file(owner):
    print(" ======= Checking events for %s " % date.today())
    events_url = "https://api.github.com/users/{}/events/public".format(owner)
    response = requests.get(events_url)
    response_content = json.loads(response.text)

    event_msgs = {}
    for i, val in enumerate(response_content):
        event_msgs[(val['type'])] = (val['repo']['name'])
    print(event_msgs)
    writes_to_s3("events", event_msgs)


def write_pulls_file(owner, repo):
    print(" ======= Checking pulls for %s " % date.today())
    pulls_url = "https://api.github.com/repos/{}/{}/pulls".format(owner, repo)
    response = requests.get(pulls_url)
    response_content = json.loads(response.text)

    pull_msgs = {}
    for i, val in enumerate(response_content):
        pull_msgs[(val['url'])] = (val['created_at'])
    print(pull_msgs)
    writes_to_s3("pulls", pull_msgs)


def write_to_json_file(operation, response_dict):
    file_path = Path('resources/%s.json' % operation)
    json_str = json.dumps(response_dict, indent=4) + '\n'
    file_path.write_text(json_str, encoding='utf-8')


def write_files_once_a_day():
    start_time = time.time()
    while True:
        write_commits_file("isuruuy429", "Masters")
        write_events_file("isuruuy429")
        write_pulls_file("octocat", "hello-world")
        time.sleep(90000 - ((time.time() - start_time) % 90000))


def writes_to_s3(operation, response_dict):
    aws_access_key_id = ''
    aws_secret_access_key = ''

    s3 = boto3.resource('s3', aws_access_key_id=aws_access_key_id, aws_secret_access_key=aws_secret_access_key)
    obj = s3.Object('isuruuy-deviq', '{}.json'.format(operation))
    obj.put(Body=json.dumps(response_dict), )


write_files_once_a_day()
