
/*===============================================DDL===============================================*/
CREATE TABLE networks
(
	network_id INT NOT NULL PRIMARY KEY,
	network_name VARCHAR(100) NOT NULL,
	input_neurons INT NOT NULL,
	hidden_neurons INT NULL,
	output_neurons INT NOT NULL,
	network BLOB NULL,
	creation_date varchar(20) NOT NULL,
	training_method varchar(20) NOT NULL,
	task_type INT NOT NULL,
	comment varchar(250) NULL
);

insert into
	networks(network_id,network_name,input_neurons,hidden_neurons,output_neurons,network,creation_date,training_method,task_type,comment)
values
	(1,'dummy',0,0,0,NULL,'dummy','dummy',0,'dummy');

CREATE TABLE training_results
(
	results_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	network_id INT NOT NULL,
	input_values VARCHAR(4000),
	actual_output_values varchar(3000),
	ideal_output_values varchar(3000),
	input_values_denormalized varchar(4000),
	actual_output_values_denormalized varchar (3000),
	ideal_output_values_denormalized varchar (3000),
	INDEX (network_id),
	FOREIGN KEY (network_id) references networks (network_id)
);