-- =========================
-- PART-1
----------------------------
-- DATABASE
-- =========================
create database Contract_system;


-- =========================
-- PART-2
----------------------------
-- TABLES
-- =========================

--1.Vendors
create table vendors(
        vendor_id serial primary key,
        name varchar(100) not null,
        email varchar(100) unique,
        phone varchar(15)
);

--2.Departments
create table departments(
        dept_id serial primary key,
        dept_name varchar(100) unique not null
);

--3.Users
create table users(
        user_id serial primary key,
        name varchar(100) not null,
        role varchar(30) not null,
        dept_id int references departments(dept_id)
);

--4.Contracts
create table contracts(
        contract_id serial primary key,
        vendor_id int not null references vendors(vendor_id),
        dept_id int not null references departments(dept_id),
        contract_value numeric(10,2) check (contract_value >= 0),
        start_date date,
        end_date date,
        status varchar(30) default 'DRAFT',
    --Service Level Agreement(sla)
        sla_days int check (sla_days >= 0)
);

--5.Obligations
create table obligations(
        obligation_id serial primary key,
        contract_id int references contracts(contract_id),
        description varchar(200),
        due_date date,
        completed_date date,
        status varchar(30) default 'PENDING'
);

--6.Payments
create table payments(
        payment_id serial primary key,
        contract_id int references contracts(contract_id),
        amount numeric(12,2) check (amount >= 0),
        payment_date date,
        status varchar(30) default 'PENDING'
);

--7.Risk Alerts
create table risk_alerts(
        alert_id serial primary key,
        contract_id int references contracts(contract_id),
        message varchar(200),
        risk_level varchar(20),
        created_at timestamp default current_timestamp
);

--8.Risk Scores
create table risk_scores(
        contract_id int primary key references contracts(contract_id),
        score int default 0,
        risk_level varchar(20)
);

--9.Audit Log
create table audit_log(
        log_id serial primary key,
        action varchar(200),
        action_time timestamp default current_timestamp
);


-- =========================
-- PART-3
----------------------------
-- INDEXES : Improves performance
-- Indexes are used to speed up data retrieval by allowing fast access to rows in a table.
-- =========================

-- Contracts table
create index idx_contract_vendor on contracts(vendor_id);
create index idx_contract_dept on contracts(dept_id);
create index idx_contract_status on contracts(status);

-- Obligations table
create index idx_obligation_contract on obligations(contract_id);
create index idx_obligation_due_date on obligations(due_date);

-- Payments table
create index idx_payment_contract on payments(contract_id);
create index idx_payment_date on payments(payment_date);


-- =========================
-- PART-4
----------------------------
-- TRIGGERS
-- =========================

-- 1. Overdue Obligation Trigger : This trigger automatically creates a risk alert when a new obligation is already overdue and still pending.
create or replace function  fun_obligation_delay()
returns trigger as $$
begin
	if new.due_date < current_date and new.status = 'PENDING' then
		insert into risk_alerts(contract_id, message, risk_level)
		values (new.contract_id, 'Overdue Obligation detected', 'HIGH');
end if;
return new;
end;
$$ language plpgsql;


create trigger trigger_obligation_delay
after insert on obligations
for each row
execute function fun_obligation_delay();


-- 2.Overdue Update Trigger : This trigger automatically generates a risk alert when an updated obligation becomes overdue and remains pending.
create or replace function fun_obligation_update()
returns trigger as $$
begin
	if new.due_date < current_date and new.status = 'PENDING' then
		insert into risk_alerts(contract_id, message, risk_level)
		values (new.contract_id, 'Pending obligation has exceeded its due date', 'HIGH');
end if;
return new;
end;
$$ language plpgsql;


create trigger trigger_obligation_update
after update on obligations
for each row
execute function fun_obligation_update();


-- 3.Late Payment Trigger :  COALESCE = “give me the first non-NULL value”
create or replace function fun_payment_late()
returns trigger as $$
begin
	if coalesce(new.status,'') = 'LATE' then
		insert into risk_alerts(contract_id, message, risk_level)
		values(new.contract_id, 'Late payment detected', 'MEDIUM');
end if;
return new;
end;
$$ language plpgsql;


create trigger trigger_payment_late
after insert on payments
for each row
execute function fun_payment_late();

-- 4.Late Payment Update Trigger :
create or replace function fun_payment_update()
returns trigger as $$
begin
	if coalesce(new.status,'') = 'LATE' then
		insert into risk_alerts(contract_id, message, risk_level)
		values(new.contract_id, 'Payment updated to late', 'MEDIUM');
end if;
return new;
end;
$$ language plpgsql;


create trigger trigger_payment_update
after update on payments
for each row
execute function fun_payment_update();

-- 5.Audit Trigger :
create or replace function fun_contract_audit()
returns trigger as $$
begin
	if old.status is distinct from new.status then
		insert into audit_log(action)
		values(
				'Contract ' || new.contract_id ||
				' updated from status ' || old.status ||
				' to ' || new.status
		);
end if;
return new;
end;
$$ language plpgsql;


create trigger trigger_contract_update
after update on contracts
for each row
execute function fun_contract_audit();


-- =========================
-- PART-5
----------------------------
-- STORED PROCEDURE :A stored procedure is used to save a set of SQL queries and run them together whenever needed.
-- Stored procedures are used to store and execute a set of SQL statements as a single reusable unit.
-- =========================

create or replace procedure calculate_risk_score(c_id int)
language plpgsql
as $$
declare
delay_risk_score int := 0;
	payment_risk_score int := 0;
	value_risk_score int := 0;
	total_risk_score int := 0;

begin

	--Delay score
select count(*) * 15 into delay_risk_score
from obligations
where contract_id = c_id and status = 'DELAYED';

--Payment score
select count(*) * 25 into payment_risk_score
from payments
where contract_id = c_id and status = 'LATE';

--Contract Value score
select case when contract_value > 55000 then 5 else 0 end
into value_risk_score
from contracts where contract_id = c_id limit 1;

--Total score
total_risk_score := delay_risk_score + payment_risk_score + value_risk_score;

	--Insert or update
insert into risk_scores(contract_id, score, risk_level)
values(c_id, total_risk_score,
       case
           when total_risk_score > 60 then 'HIGH'
           when total_risk_score > 25 then 'MEDIUM'
           else 'LOW'
           end
      )
    on conflict (contract_id)
	do update set
    score = excluded.score,
               risk_level = excluded.risk_level;
end;
$$;


-- =========================
-- PART-6
----------------------------
-- VIEW :
-- =========================
create or replace view high_risk_contracts as
select
    c.contract_id,
    v.name as vendor_name,
    c.contract_value,
    r.score,
    r.risk_level
from contracts c
         join vendors v on c.vendor_id = v.vendor_id
         join risk_scores r on c.contract_id = r.contract_id
where r.risk_level = 'HIGH';


-- =========================
-- PART-7
----------------------------
-- INSERT DATA :
-- =========================

--Vendors
insert into vendors (name, email, phone) values
            ('Tech Solutions', 'techsol@gmail.com', '9876543210'),
            ('Global Tech Solution', 'globaltechsol@gmail.com', '9126734598'),
            ('DotNet Pvt Ltd', 'cloudnet@gmail.com', '9988772345');

--Departments
insert into departments (dept_name) values
            ('Marketing'),
            ('Human Resources(HR)'),
            ('Sales');

--Users
insert into users (name, role, dept_id) values
            ('Shivani', 'ANALYST', 1),
            ('Isha', 'ADMIN', 2),
            ('Neha', 'MANAGER', 3);

--Contracts
insert into contracts (vendor_id, dept_id, contract_value, start_date, end_date, status, sla_days) values
            (1, 1, 150000, '2026-03-01', '2026-11-28', 'ACTIVE', 8),
            (2, 2, 95000, '2026-06-12', '2026-08-30', 'SUSPENDED', 10),
            (3, 3, 60000, '2026-01-20', '2026-04-25', 'EXPIRED', 6);

--Obligations
insert into obligations (contract_id, description, due_date, completed_date, status) values
            (1, 'Security Compliance Review & Audit', '2026-05-15', NULL, 'DELAYED'),
            (1, 'Performance & Load Testing Assessment', '2026-07-05', NULL, 'PENDING'),
            (2, 'Final System Integration & Deployment', '2026-06-02', '2026-06-02', 'COMPLETED'),
            (3, 'Contract Performance Review & Reporting', '2026-03-11', NULL, 'DELAYED'),
            (1, 'Extra delay 1', '2026-01-01', NULL, 'DELAYED'),
            (1, 'Extra delay 2', '2026-01-01', NULL, 'DELAYED');


--Payments
insert into payments (contract_id, amount, payment_date, status) values
            (1, 35000, '2026-03-10', 'LATE'),
            (1, 20000, '2026-04-10', 'PENDING'),
            (2, 41000, '2026-05-15', 'PAID'),
            (3, 18000, '2026-03-20', 'LATE');

--Calculate Risk Scores
call calculate_risk_score(1);
call calculate_risk_score(2);
call calculate_risk_score(3);



-- =========================
-- PART-8
----------------------------
-- TEST AUDIT LOG
-- =========================

-- Update contract to trigger audit log
update contracts
set status = 'TERMINATED'
where contract_id = 1;



--Testing
select * from vendors;
select * from departments;
select * from users;
select * from contracts;
select * from obligations;
select * from payments;
select * from risk_alerts;
select * from risk_scores;
select * from audit_log;