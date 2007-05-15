/* Basic database table for storing pages. */

create table page (
	path varchar(255) primary key,
	data text not null
);
