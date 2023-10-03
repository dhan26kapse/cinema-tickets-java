# cinema-tickets-java

This project is a java application for purchasing cinema tickets based on some business rules and constraints.

# Table of Contents
- [Overview](#overview)
- [Business Rules](#business-rules)
- [Constraints](#constraints)
- [Assumptions](#assumptions)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [Running tests](#tests)

## Overview

This Java application allows users to purchase cinema tickets based on various ticket types (Infant, Child, and Adult) while adhering to specific business rules and constraints. The application calculates the total price for the requested tickets, processes payments, and reserves seats.

## Business Rules

- There are three types of tickets: Infant, Child, and Adult.
- Ticket prices are based on the type of ticket.
- Multiple tickets can be purchased at once, but there is a maximum limit.
- Infants do not pay for a ticket and do not have allocated seats.
- Child and Infant tickets cannot be purchased without an Adult ticket.

## Ticket Prices

| Ticket Type | Price |
|-------------|-------|
| Infant      | £0    |
| Child       | £10   |
| Adult       | £20   |

## Constraints

- The `TicketService` interface cannot be modified.
- Code in the `thirdparty.*` packages cannot be modified.
- The `TicketTypeRequest` should be an immutable object.

## Assumptions

- All accounts with an ID greater than zero are considered valid.
- The `TicketPaymentService` and `SeatReservationService` implementations are external providers without defects.
- Payments always go through once a payment request is made.
- Seats will always be reserved once a reservation request is made.

## Getting Started

### Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) installed.
- An Integrated Development Environment (IDE) such as IntelliJ IDEA.
- Git for version control.

### Installation

1. Clone the repository:

   ```shell
   git clone https://github.com/your-username/cinema-tickets.git
Open the project in your preferred IDE.

## Usage

To use this application, follow these steps:

Initialize the TicketServiceImpl with appropriate implementations of TicketPaymentService and SeatReservationService.
Use the purchaseTickets method to purchase cinema tickets. Provide the necessary ticket type requests and account ID.
The application will calculate the total price, process payments, and reserve seats according to the specified business rules.

## Tests

Try running the tests provided in the tests folder. File is named TicketServiceImplTest.
This test file verifies all the requried constraints to be satisfied for purchasing a ticket and checks for valid and invalid
purchase tests.
