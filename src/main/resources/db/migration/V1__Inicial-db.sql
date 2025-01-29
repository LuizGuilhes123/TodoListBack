-- Tabela de cargos (Positions)
create table tb_cargo (
    id bigint not null auto_increment,
    authority varchar(255) not null,
    primary key (id)
);

-- Tabela de tarefas (Tasks)
create table tb_tarefa (
    cost decimal(38,2),
    due_date date,
    position integer,
    id bigint not null auto_increment,
    user_id binary(16), -- Adaptado para UUID
    name varchar(255),
    favorite boolean, -- Coluna para indicar tarefa favorita
    primary key (id),
    constraint UK_user_name unique (user_id, name) -- Constrangimento de unicidade (user_id, name)
);

-- Tabela de usuários (Users)
create table tb_user (
    id binary(16) not null, -- Adaptado para UUID
    email varchar(255) unique, -- Constrangimento de e-mail único
    name varchar(255),
    password varchar(255),
    notification boolean, -- Coluna para notificações
    img_url text, -- URL da imagem, com nome e definição conforme entidade
    primary key (id)
);

-- Tabela de relacionamento entre usuários e cargos (Many-to-Many)
create table tb_user_cargo (
    cargo_id bigint not null,
    user_id binary(16) not null, -- Adaptado para UUID
    primary key (cargo_id, user_id)
);

-- Constrangimentos e chaves estrangeiras

-- Chave estrangeira para vincular as tarefas ao usuário (Many-to-One)
alter table tb_tarefa add constraint FK_task_user foreign key (user_id) references tb_user (id);

-- Chaves estrangeiras para o relacionamento entre usuários e cargos
alter table tb_user_cargo add constraint FK_user_cargo_user foreign key (user_id) references tb_user (id);
alter table tb_user_cargo add constraint FK_user_cargo_cargo foreign key (cargo_id) references tb_cargo (id);
