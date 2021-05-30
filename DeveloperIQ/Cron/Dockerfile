FROM python:3.8

WORKDIR /Commits

COPY requirements.txt .

RUN pip install -r requirements.txt

COPY ./app ./app

ARG AWS_ACCESS_KEY_ID
ENV AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}

ARG AWS_SECRET_ACCESS_KEY
ENV AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}

CMD ["python", "./app/app.py"]
