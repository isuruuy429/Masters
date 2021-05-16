# Distributed Prime Number Detection System

In this system it has been implemented a consensus based Prime Number Detection Distributed
System. The implementation is based on Paxos for consensus.

In simple terms, consensus is agreement. It is mostly found in distributed system
implementations, where a collection of computers needs to agree on some activity or decision,
such as who should get access to resource, who will be in charge of elections and agreement of
ordering a set of events and etc.

Paxos is the algorithm that has been used to get the consensus in a distributed system. It selects a
single value among one or more values that are proposed by proposers, by getting the decision of
the acceptors.

Characters in Paxos
- Proposers: Receives requests from clients, solves the problem and propose the answers
to the acceptors.
- Acceptors: Verifies the values that come from proposers and vote for a certain value.
- Learner: Announce the outcome

This system verifies the given number is a Prime number in a distributed manner.
Initially, the master node is decided through an election algorithm. It has been used the Bully
Election algorithm to elect the master node. This system is connected with a Service Registry
which holds the information such as service name, port and the node ID of all the nodes. Then
the master node communicates with the Service Registry and get all the active nodes in the
cluster and decide who are the Proposers, Acceptors and Learners in the system and
communicate that information to the respective nodes.

Then the master creates the schedule for the proposer nodes. As an example, the schedule is if
there ae re four proposer nodes, the number to check for prime is 23412, it should divide this
number into four equal groups of ranges. As an example, node1 should start dividing the number
from 0-1000, node 2 start dividing the number from 2000 - 5000 likewise. At the time of
scheduling no node is being idle as they are designed to send heart beats to the Service Registry
to check if the master node is alive. 

After the Proposer nodes receive the schedule, they start diving the number as per their
respective range.

Then the Master node needs to communicate the learner nodes that how many proposers exist.
That information is needed for learners to decide the final outcome of the algorithm.
Once the Proposer nodes complete dividing numbers, they communicate the result to the
randomly selected Acceptor node. The Acceptor nodes are selected after communicating with the
Service Registry.

If the number is divided by a certain number other than 1 and its own value, it is communicated
that as the ‘Number is not Prime, and it is divisible by the particular number’. If the number is
not divisible by any of the numbers in between, it is provided the result as ‘Number is Prime’.
The Acceptor nodes receive the message from Proposers. If it gets the result that the ‘Number is
not Prime’, it needs to re-verify the result and check the validity of the message. If the Acceptor
node get the message as the ‘Number is Prime’, it would not reverify the result and forward the
result to the learner.

The Acceptor nodes communicate with the Service Registry and find out the Learner nodes and
send the results.

The learner node counts the number of messages sent by the Acceptor nodes. If there is even one
message says that the number is not prime, it would be decided that the number is not prime. If
all the messages from the Acceptors say that the number is prime, the Learner decides that it is
prime and terminates the algorithm. 
