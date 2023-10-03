/**
 * @author Dhanashree Kapse
 * */
package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.logging.Logger;

/** Implementation of Ticket Service Interface {@link TicketService} that provide methods for
 * purchasing tickets, calculate ticket prices and handling payment and seat reservations.
 * @see TicketService
 * @see TicketTypeRequest
 * @see TicketPaymentService
 * @see SeatReservationService
 * */
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = Logger.getLogger(TicketServiceImpl.class.getName());

    private static final int MAX_TICKETS_PER_PURCHASE = 20;
    private static final int ADULT_TICKET_PRICE = 20;
    private static final int CHILD_TICKET_PRICE = 10;
    private static final int INFANT_TICKET_PRICE = 0;

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;

    }

    /** Purchase tickets method for a given account and ticket type requests.
     * <p>This method calculates the total price for the requested tickets based on the type and quantity of tickets,
     * initiates the payment using {@link TicketPaymentService} and reserves the seats using {@link SeatReservationService}
     * upon successful payment.</p>
     * @param accountId The account identifier of the ticket purchaser.
     * @param ticketTypeRequests Array of objects representing the requested ticket types and quantities.
     * @throws InvalidPurchaseException If the purchase request is invalid or if the payment process fails.
     * */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        validateInputForTickets(ticketTypeRequests);

        int totalTickets = 0;
        int totalInfantTickets = 0;
        int totalChildTickets = 0;
        int totalAdultTickets = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {
            TicketTypeRequest.Type ticketType = request.getTicketType();
            int numberOfTickets = request.getNoOfTickets();
            totalTickets += numberOfTickets;

            switch (ticketType) {
                case ADULT:
                    totalAdultTickets += numberOfTickets;
                    break;
                case CHILD:
                    totalChildTickets += numberOfTickets;
                    break;
                case INFANT:
                    break;
            }
        }

        validateTicketPurchaseRequest(totalTickets, totalInfantTickets, totalChildTickets, totalAdultTickets);
        int totalPrice = calculateTotalTicketsPrice(totalInfantTickets, totalChildTickets, totalAdultTickets);

        logger.info("Total tickets in purchase request:" +totalTickets);

        if(paymentProcessForTickets(accountId, totalPrice)) {
            int totalSeatsToAllocate = totalAdultTickets + totalChildTickets;
            reserveSeats(accountId, totalSeatsToAllocate);
        } else {
            logger.warning("Payment failed for account ID:" + accountId);
            throw new InvalidPurchaseException("fail to purchase tickets");
        }
    }

    /** validates the input for the purchase of tickets
     * <p>ensures the provided array of {@link TicketTypeRequest} is not null and that there is at least one request made.</p>
     * @param ticketTypeRequests Array of objects representing the requested ticket types and quantities.
     * @throws InvalidPurchaseException If the purchase request is invalid or if the payment process fails.
     * */
    private void validateInputForTickets(TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException("Invalid input");
        }
    }

    /** validates the ticket purchase request against the business rules and constraints.
     * <p> Checks for the maximum limit of purchase of tickets, if there is at least one adult ticket while
     * purchasing tickets for children and infants.</p>
     * @param totalTickets Total number of tickets requested
     * @param totalInfantTickets Total number of infants tickets requested
     * @param totalChildTickets Total number of children tickets requested
     * @param totalAdultTickets Total number of adults tickets requested
     * @throws InvalidPurchaseException If the purchase request is invalid or if the payment process fails.
     * */
    private void validateTicketPurchaseRequest(int totalTickets, int totalInfantTickets, int totalChildTickets, int totalAdultTickets) throws InvalidPurchaseException {
        if (totalTickets > MAX_TICKETS_PER_PURCHASE) {
            logger.warning("Limit exceeded! total tickets at one go is 20");
            throw  new InvalidPurchaseException("Limit exceeded! total tickets at one go is 20");
        }
        if (totalAdultTickets < 1) {
            logger.warning("There must be one adult ticket present to buy a child ticket or an infant ticket");
            throw  new InvalidPurchaseException("There must be one adult ticket present");
        }
        if ((totalChildTickets > 0 || totalInfantTickets > 0) && totalAdultTickets < 1) {
            logger.warning("no child or infant ticket can be purchased without an adult ticket");
            throw  new InvalidPurchaseException("no child or infant ticket can be purchased without an adult ticket");
        }
    }

    /** Calculate the total price of requested tickets.
     *  <p>Total price is calculated based on the number of adult, child and infant tickets purchased.</p>
     * @param totalInfantTickets Total number of infants tickets requested
     * @param totalChildTickets Total number of children tickets requested
     * @param totalAdultTickets Total number of adults tickets requested
     * @return The total price of the requested tickets.
     * */
    private int calculateTotalTicketsPrice(int totalInfantTickets, int totalChildTickets, int totalAdultTickets) {
        return totalAdultTickets * ADULT_TICKET_PRICE + totalChildTickets * CHILD_TICKET_PRICE +
                totalInfantTickets * INFANT_TICKET_PRICE;
    }

    /** Initiates the payment process for purchasing tickets.
     * @see TicketPaymentService interface for making payment.
     * @param accountId The account identifier of the ticket purchaser.
     * @param totalAmountToPay The total amount to be paid for tickets.
     * @return True if the payment is successful, false otherwise.
     * */
    private boolean paymentProcessForTickets(Long accountId, int totalAmountToPay) {
        try {
            ticketPaymentService.makePayment(accountId, totalAmountToPay);
            logger.info("Payment successful for account ID:" +accountId + ", Amount:" +totalAmountToPay);
            return true;
        } catch (InvalidPurchaseException e) {
            logger.severe("Payment error for account ID:" +accountId);
            return false;
        }
    }

    /** Reserve seats for the purchased tickets.
     * @see SeatReservationService interface for reserving seats.
     * @param accountId The account identifier of the ticket purchaser.
     * @param totalSeatsToAllocate The total number of seats to be reserved.
     * @throws InvalidPurchaseException If the purchase request is invalid or if the payment process fails.
     * */
    private void reserveSeats(Long accountId, int totalSeatsToAllocate) throws InvalidPurchaseException {
        seatReservationService.reserveSeat(accountId, totalSeatsToAllocate);
        logger.info("Seats reserved:" + totalSeatsToAllocate);
    }
}
