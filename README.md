# akka-cluster

docker run -it --network="host" -p 2551:2551 --entrypoint bin/pong-actor akka-cluster:0.1 2551

docker run -it --network="host" -p 2551:2552 --entrypoint bin/pong-actor akka-cluster:0.1 2552

docker run -it --network="host" -p 2551:2553 --entrypoint bin/pong-actor akka-cluster:0.1 2553

docker run -it --network="host" -p 2551:2554 --entrypoint bin/ping-actor akka-cluster:0.1 2554
