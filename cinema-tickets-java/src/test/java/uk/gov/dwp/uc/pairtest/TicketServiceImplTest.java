/** Unit Test Case class for {@link uk.gov.dwp.uc.pairtest.TicketServiceImpl}
 * This class holds the test cases to verify the behaviour of the purchase process.
 * */

package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

public class TicketServiceImplTest {

    @Mock
    private TicketPaymentService ticketPaymentService;

    @Mock
    private SeatReservationService seatReservationService;

    @InjectMocks
    private TicketServiceImpl ticketService;


    @Before
    public void setUp() throws InvalidPurchaseException {
        MockitoAnnotations.openMocks(this);
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
    }

    /** Test Case for a valid ticket purchase.
     * <p> Verifies that the purchaseTickets method successfully processes a valid ticket purchase request,
     * makes the payment and reserves the seats.</p>
     * @throws InvalidPurchaseException if the test case encounters an unexpected exception.
     * */
    @Test
    public void purchaseTickets_Valid() throws InvalidPurchaseException {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        doNothing().when(ticketPaymentService).makePayment(anyLong(), anyInt());
        doNothing().when(seatReservationService).reserveSeat(anyLong(), anyInt());

        ticketService.purchaseTickets(1L, adultTicket, childTicket);
    }

    /** Test Case for a invalid ticket purchase.
     * <p> Verifies that the purchaseTickets method correctly throws an {@link InvalidPurchaseException} for
     * an invalid ticket purchase request.</p>
     * @throws InvalidPurchaseException if the test case encounters an unexpected exception.
     * */
    @Test
    public void purchaseTickets_InValid() throws InvalidPurchaseException {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        doNothing().when(ticketPaymentService).makePayment(anyLong(), anyInt());
        doNothing().when(seatReservationService).reserveSeat(anyLong(), anyInt());

        assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(1L, adultTicket, childTicket);
        });
    }
}