create table if not exists cards (
  id int not null auto_increment primary key,
  mobile_number varchar(20) not null,
  card_number varchar(100) not null,
  card_type varchar(100) not null,
  total_limit int not null,
  amount_used int not null,
  available_amount int not null,
  created_at date not null,
  created_by varchar(30) not null,
  updated_at date default null,
  updated_by varchar(30) default null
);
