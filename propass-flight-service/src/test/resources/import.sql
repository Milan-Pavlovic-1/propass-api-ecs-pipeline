DELETE FROM flights; -- reset db state

INSERT INTO flights (id, airline_id, from_location_name, from_location_code, from_location_type, to_location_name, to_location_code, to_location_type)
VALUES (1, 1, 'Amman, Jordan', 'AMM', 'Airport', 'Muscat, Oman', 'MCT', 'Airport');
INSERT INTO flights (id, airline_id, from_location_name, from_location_code, from_location_type, to_location_name, to_location_code, to_location_type)
VALUES (2, 1, 'Amman, Jordan', 'AMM', 'Airport', 'Cairo, Egypt', 'CAI', 'Airport');
INSERT INTO flights (id, airline_id, from_location_name, from_location_code, from_location_type, to_location_name, to_location_code, to_location_type)
VALUES (3, 2, 'Cairo, Egypt', 'CAI', 'Airport', 'Muscat, Oman', 'MCT', 'Airport');