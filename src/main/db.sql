-- индексы
create table users (
    id serial primary key,
    email varchar(255) unique not null,
    password varchar(255) not null,
    name varchar(100)
);

create table roles (
    id serial primary key,
    name varchar(50) unique not null,
    description text,
    authority varchar(50) unique not null,
    created_at timestamp,
    updated_at timestamp
);

create table user_roles (
    user_id int references users(id),
    role_id int references roles(id),
    primary key (user_id, role_id)
);

create table project (
    id serial primary key ,
    name varchar(100) not null ,
    description text,
    start_date date,
    end_date date
);

create table project_users (
    project_id int references project(id),
    user_id int references users(id),
    primary key (project_id, user_id)
);

create table board (
    id serial primary key ,
    name varchar(100) not null,
    project_id int references project(id)
);

create table sprint (
    id serial primary key ,
    name varchar(100) not null,
    start_date date,
    end_date date,
    project_id int references project(id)
);

create table task (
    id serial primary key ,
    title varchar(255) not null,
    description text,
    status varchar(20) check (status in ('TODO', 'IN_PROGRESS', 'DONE')),
    priority int,
    due_date timestamp,
    attachment_path varchar(255),
    author_id int references users(id),
    assignee_id int references users(id),
    board_id int references board(id),
    sprint_id int references sprint(id)
);



-- индексы
CREATE INDEX idx_task_status ON task(status);
CREATE INDEX idx_user_email ON users(email);