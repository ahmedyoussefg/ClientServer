import base64
from locust import HttpUser, task, between

class MyServerUser(HttpUser):
    wait_time = between(1, 3)

    @task
    def get_request(self):
        self.client.get("/index.html")

    @task
    def post_request(self):
        data = "text"
        encoded_data = base64.b64encode(data.encode()).decode()
        encoded_data += "\n"
        self.client.post("/test_index.html", data=encoded_data, headers={"Content-Type": "text/plain"})

