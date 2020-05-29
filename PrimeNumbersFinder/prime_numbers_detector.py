import random


def is_prime_number():
    file_name = "/Users/IsuruUyanage/Desktop/Masters/DC/assignment/PrimeNumbers.txt"
    with open(file_name, 'r') as f:
        lines = f.read().splitlines()
        random_number = int(random.choice(lines))

    if random_number <= 1:
        return f'{random_number} number'
    else:
        for number in range(2, random_number):
            if random_number % number == 0 and random_number != number:
                print(f"{random_number} is divisible by {number}. {random_number} is not a prime number")
                return f"{random_number} is divisible by {number}. {random_number} is not a prime number"
        print(f"{random_number} is a prime number")
        return f"{random_number} is a prime number"

