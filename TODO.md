# TODO: Resolve Issues in Ticket and Service Tracking System

## Issues Identified
1. **TicketDetailsServlet Bug**: `getTicketDetails` in TicketDAO fetches ticket history instead of ticket details, causing SQLException when accessing ticket fields.
2. **Duplicate Methods**: `getTicketDetails` and `getTicketHistory` are identical in TicketDAO.
3. **Security Issue**: Stack trace printed to response in CreateTicket servlet.
4. **Insecure Password Hashing**: SHA-256 without salt in LoginServlet (optional, requires DB changes).

## Tasks
- [ ] Fix `getTicketDetails` in TicketDAO to fetch ticket details with proper joins.
- [ ] Remove duplicate `getTicketHistory` method from TicketDAO.
- [ ] Update TicketDetailsServlet to handle the corrected `getTicketDetails`.
- [ ] Remove stack trace printing in CreateTicket servlet error handling.
- [ ] Test the application to ensure ticket details display correctly.
