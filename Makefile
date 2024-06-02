db-up:
	docker run --name my-db -p 5432:5432 -e POSTGRES_PASSWORD=password -e POSTGRESQL_DATABASE=example bitnami/postgresql:latest
db-stop:
	docker stop my-db
db-down:
	docker stop my-db
	docker rm my-db

test-db-up:
	docker run --name my-new-test-db1 -p 5434:5432 -e POSTGRES_PASSWORD=password -e POSTGRESQL_DATABASE=test-example bitnami/postgresql:latest
test-db-stop:
	docker stop my-new-test-db1
test-db-down:
	docker stop my-new-test-db1
	docker rm my-new-test-db1