DELETE FROM vouchers_history; -- reset db state
DELETE FROM vouchers; -- reset db state
DELETE FROM vouchers_type; -- reset db state

INSERT INTO vouchers_type (id, name, airline_id, price_formula, restriction_formula) VALUES (1, 'first', 1, '{"vouchers":true,"savingsFormula":"20"}', '{"flights":[123,456],"minDaysBefore":10,"class":"economy"}');
INSERT INTO vouchers_type (id, name, airline_id, price_formula, restriction_formula) VALUES (2, 'second', 1, '{"vouchers":true,"savingsFormula":"20"}', '{"flights":[789],"minDaysBefore":20,"class":"economy"}');
INSERT INTO vouchers_type (id, name, airline_id, price_formula, restriction_formula) VALUES (3, 'third', 1, '{"vouchers":true}', '{"flights":[456,789],"minDaysBefore":30}');

INSERT INTO vouchers (id, total_amount, used_amount, flight_id, created_by, voucher_type_id) VALUES (1, 10, 2, 123, 'john@constantine.com', 1);
INSERT INTO vouchers (id, total_amount, used_amount, flight_id, created_by, voucher_type_id) VALUES (2, 5, 1, 456, 'john@wick.com', 1);
INSERT INTO vouchers (id, total_amount, used_amount, flight_id, created_by, voucher_type_id) VALUES (3, 15, 11, 456, 'john@constantine.com', 1);

INSERT INTO vouchers_history (id, voucher_id, ticket_ref) VALUES (1, 1, 'ticket-123');
INSERT INTO vouchers_history (id, voucher_id, ticket_ref) VALUES (2, 2, 'ticket-456');
INSERT INTO vouchers_history (id, voucher_id, ticket_ref) VALUES (3, 1, 'ticket-789');