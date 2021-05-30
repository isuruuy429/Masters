FROM python:3.8

WORKDIR /Events

COPY requirements.txt .

RUN pip install -r requirements.txt

COPY ./app ./app

ARG COMMITS
ENV COMMITS=${COMMITS}

ARG EVENTS
ENV EVENTS=${EVENTS}

ARG PULLS
ENV PULLS=${PULLS}

CMD ["python", "./app/app.py"]
