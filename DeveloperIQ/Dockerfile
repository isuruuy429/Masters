FROM python:3.8

ADD resources .
ADD create_json_files.py .
ADD app.py .

COPY requirements.txt .
RUN pip install -r requirements.txt

CMD [ "python", "./app.py" ]

