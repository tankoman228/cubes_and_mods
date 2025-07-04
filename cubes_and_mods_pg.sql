--
-- PostgreSQL database dump
--

-- Dumped from database version 14.15 (Ubuntu 14.15-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.15 (Ubuntu 14.15-0ubuntu0.22.04.1)

-- Started on 2025-05-26 22:42:12 +07

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3466 (class 1262 OID 16516)
-- Name: cubes_and_mods; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE cubes_and_mods WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'ru_RU.UTF-8';


\connect cubes_and_mods

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 222 (class 1259 OID 16598)
-- Name: admins; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.admins (
    id integer NOT NULL,
    username character varying(64),
    password_hash character varying(256),
    can_view_stats boolean,
    can_view_logs boolean,
    can_clients boolean,
    can_hosts boolean,
    can_orders boolean,
    can_servers boolean,
    can_monitor_srv boolean,
    can_tech_support boolean,
    can_tariffs boolean,
    can_admins boolean
);


--
-- TOC entry 221 (class 1259 OID 16597)
-- Name: admins_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.admins_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3467 (class 0 OID 0)
-- Dependencies: 221
-- Name: admins_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.admins_id_seq OWNED BY public.admins.id;


--
-- TOC entry 209 (class 1259 OID 16517)
-- Name: backups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.backups (
    id integer NOT NULL,
    id_host integer NOT NULL,
    size_kb bigint NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    name character varying(64)
);


--
-- TOC entry 210 (class 1259 OID 16521)
-- Name: backups_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.backups_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3468 (class 0 OID 0)
-- Dependencies: 210
-- Name: backups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.backups_id_seq OWNED BY public.backups.id;


--
-- TOC entry 217 (class 1259 OID 16539)
-- Name: clients; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.clients (
    id integer NOT NULL,
    email character varying(128) NOT NULL,
    password character varying(256) NOT NULL,
    banned boolean DEFAULT false NOT NULL,
    additional_info character varying DEFAULT ''::character varying NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 16627)
-- Name: games; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.games (
    id integer NOT NULL,
    name character varying
);


--
-- TOC entry 223 (class 1259 OID 16626)
-- Name: games_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.games_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3469 (class 0 OID 0)
-- Dependencies: 223
-- Name: games_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.games_id_seq OWNED BY public.games.id;


--
-- TOC entry 225 (class 1259 OID 16640)
-- Name: host_sharings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.host_sharings (
    id_host integer NOT NULL,
    id_client integer NOT NULL,
    id integer NOT NULL
);


--
-- TOC entry 228 (class 1259 OID 17402)
-- Name: host_sharings_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.host_sharings_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3470 (class 0 OID 0)
-- Dependencies: 228
-- Name: host_sharings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.host_sharings_id_seq OWNED BY public.host_sharings.id;


--
-- TOC entry 213 (class 1259 OID 16526)
-- Name: hosts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.hosts (
    id integer NOT NULL,
    memory_used bigint DEFAULT 0 NOT NULL,
    id_client integer NOT NULL,
    id_tariff integer NOT NULL,
    id_server integer NOT NULL,
    seconds_working integer DEFAULT 0 NOT NULL,
    name character varying(64) NOT NULL,
    description character varying(256) DEFAULT ''::character varying NOT NULL
);


--
-- TOC entry 211 (class 1259 OID 16522)
-- Name: servers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.servers (
    id integer NOT NULL,
    name character varying(64) NOT NULL,
    address character varying(64),
    cpu_name character varying(24) NOT NULL,
    cpu_threads smallint NOT NULL,
    cpu_threads_free smallint,
    ram smallint NOT NULL,
    ram_free smallint,
    memory bigint NOT NULL,
    memory_free bigint
);


--
-- TOC entry 212 (class 1259 OID 16525)
-- Name: machine_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.machine_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3471 (class 0 OID 0)
-- Dependencies: 212
-- Name: machine_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.machine_id_seq OWNED BY public.servers.id;


--
-- TOC entry 227 (class 1259 OID 16692)
-- Name: microservice_sessions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.microservice_sessions (
    ip_port character varying(25) NOT NULL,
    last_register timestamp without time zone DEFAULT now() NOT NULL,
    first_register timestamp without time zone DEFAULT now() NOT NULL,
    service_type character varying(25) NOT NULL,
    alarm boolean,
    banned boolean
);


--
-- TOC entry 214 (class 1259 OID 16531)
-- Name: mineservers_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.mineservers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3472 (class 0 OID 0)
-- Dependencies: 214
-- Name: mineservers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.mineservers_id_seq OWNED BY public.hosts.id;


--
-- TOC entry 226 (class 1259 OID 16671)
-- Name: order; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."order" (
    code character varying(128) NOT NULL,
    id_client integer NOT NULL,
    id_server integer,
    id_tariff integer NOT NULL,
    made_at timestamp without time zone DEFAULT now() NOT NULL,
    closed_at timestamp without time zone,
    confirmed boolean,
    id_host integer
);


--
-- TOC entry 215 (class 1259 OID 16532)
-- Name: tariffs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tariffs (
    id integer NOT NULL,
    name character varying(64) NOT NULL,
    cost_rub integer NOT NULL,
    ram smallint NOT NULL,
    cpu_threads smallint NOT NULL,
    memory_limit bigint NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    hours_work_max integer DEFAULT 24 NOT NULL,
    max_players integer DEFAULT 10
);


--
-- TOC entry 216 (class 1259 OID 16538)
-- Name: tariffs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.tariffs_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3473 (class 0 OID 0)
-- Dependencies: 216
-- Name: tariffs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.tariffs_id_seq OWNED BY public.tariffs.id;


--
-- TOC entry 218 (class 1259 OID 16545)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3474 (class 0 OID 0)
-- Dependencies: 218
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.clients.id;


--
-- TOC entry 219 (class 1259 OID 16546)
-- Name: versions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.versions (
    id integer NOT NULL,
    name character varying NOT NULL,
    description text NOT NULL,
    archive bytea NOT NULL,
    id_game integer NOT NULL
);


--
-- TOC entry 220 (class 1259 OID 16551)
-- Name: versions_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.versions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3475 (class 0 OID 0)
-- Dependencies: 220
-- Name: versions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.versions_id_seq OWNED BY public.versions.id;


--
-- TOC entry 3272 (class 2604 OID 16601)
-- Name: admins id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admins ALTER COLUMN id SET DEFAULT nextval('public.admins_id_seq'::regclass);


--
-- TOC entry 3258 (class 2604 OID 16552)
-- Name: backups id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.backups ALTER COLUMN id SET DEFAULT nextval('public.backups_id_seq'::regclass);


--
-- TOC entry 3269 (class 2604 OID 16556)
-- Name: clients id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clients ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 3273 (class 2604 OID 16630)
-- Name: games id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.games ALTER COLUMN id SET DEFAULT nextval('public.games_id_seq'::regclass);


--
-- TOC entry 3274 (class 2604 OID 17403)
-- Name: host_sharings id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.host_sharings ALTER COLUMN id SET DEFAULT nextval('public.host_sharings_id_seq'::regclass);


--
-- TOC entry 3262 (class 2604 OID 16554)
-- Name: hosts id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hosts ALTER COLUMN id SET DEFAULT nextval('public.mineservers_id_seq'::regclass);


--
-- TOC entry 3259 (class 2604 OID 16553)
-- Name: servers id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.servers ALTER COLUMN id SET DEFAULT nextval('public.machine_id_seq'::regclass);


--
-- TOC entry 3267 (class 2604 OID 16555)
-- Name: tariffs id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tariffs ALTER COLUMN id SET DEFAULT nextval('public.tariffs_id_seq'::regclass);


--
-- TOC entry 3271 (class 2604 OID 16557)
-- Name: versions id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.versions ALTER COLUMN id SET DEFAULT nextval('public.versions_id_seq'::regclass);


--
-- TOC entry 3298 (class 2606 OID 16603)
-- Name: admins admins_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admins
    ADD CONSTRAINT admins_pkey PRIMARY KEY (id);


--
-- TOC entry 3279 (class 2606 OID 16559)
-- Name: backups backups_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.backups
    ADD CONSTRAINT backups_pkey PRIMARY KEY (id);


--
-- TOC entry 3302 (class 2606 OID 16634)
-- Name: games games_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.games
    ADD CONSTRAINT games_pkey PRIMARY KEY (id);


--
-- TOC entry 3306 (class 2606 OID 17391)
-- Name: host_sharings host_sharings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.host_sharings
    ADD CONSTRAINT host_sharings_pkey PRIMARY KEY (id);


--
-- TOC entry 3282 (class 2606 OID 16561)
-- Name: servers machine_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.servers
    ADD CONSTRAINT machine_pkey PRIMARY KEY (id);


--
-- TOC entry 3310 (class 2606 OID 16697)
-- Name: microservice_sessions microservice_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.microservice_sessions
    ADD CONSTRAINT microservice_sessions_pkey PRIMARY KEY (ip_port);


--
-- TOC entry 3286 (class 2606 OID 16563)
-- Name: hosts mineservers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hosts
    ADD CONSTRAINT mineservers_pkey PRIMARY KEY (id);


--
-- TOC entry 3308 (class 2606 OID 17385)
-- Name: order order_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."order"
    ADD CONSTRAINT order_pkey PRIMARY KEY (code);


--
-- TOC entry 3284 (class 2606 OID 16706)
-- Name: servers servers_address_address1_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.servers
    ADD CONSTRAINT servers_address_address1_key UNIQUE (address) INCLUDE (address);


--
-- TOC entry 3289 (class 2606 OID 16565)
-- Name: tariffs tariffs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tariffs
    ADD CONSTRAINT tariffs_pkey PRIMARY KEY (id);


--
-- TOC entry 3292 (class 2606 OID 16614)
-- Name: clients uk_usermail; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clients
    ADD CONSTRAINT uk_usermail UNIQUE (email);


--
-- TOC entry 3300 (class 2606 OID 16699)
-- Name: admins uq_adm; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admins
    ADD CONSTRAINT uq_adm UNIQUE (username) INCLUDE (username);


--
-- TOC entry 3304 (class 2606 OID 16703)
-- Name: games uq_games; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.games
    ADD CONSTRAINT uq_games UNIQUE (name) INCLUDE (name);


--
-- TOC entry 3294 (class 2606 OID 16569)
-- Name: clients users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.clients
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3296 (class 2606 OID 16571)
-- Name: versions versions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.versions
    ADD CONSTRAINT versions_pkey PRIMARY KEY (id);


--
-- TOC entry 3280 (class 1259 OID 16572)
-- Name: UK_machine_name; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX "UK_machine_name" ON public.servers USING btree (name);


--
-- TOC entry 3287 (class 1259 OID 16573)
-- Name: UK_tariff_name; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX "UK_tariff_name" ON public.tariffs USING btree (name);


--
-- TOC entry 3290 (class 1259 OID 16615)
-- Name: UK_usr_email; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX "UK_usr_email" ON public.clients USING btree (email);


--
-- TOC entry 3311 (class 2606 OID 16577)
-- Name: backups fk_backup_mineserver; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.backups
    ADD CONSTRAINT fk_backup_mineserver FOREIGN KEY (id_host) REFERENCES public.hosts(id);


--
-- TOC entry 3312 (class 2606 OID 16582)
-- Name: hosts fk_mineserver_machine; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hosts
    ADD CONSTRAINT fk_mineserver_machine FOREIGN KEY (id_server) REFERENCES public.servers(id) NOT VALID;


--
-- TOC entry 3318 (class 2606 OID 16677)
-- Name: order fk_order_client; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."order"
    ADD CONSTRAINT fk_order_client FOREIGN KEY (id_client) REFERENCES public.clients(id);


--
-- TOC entry 3320 (class 2606 OID 16687)
-- Name: order fk_order_server; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."order"
    ADD CONSTRAINT fk_order_server FOREIGN KEY (id_server) REFERENCES public.servers(id);


--
-- TOC entry 3319 (class 2606 OID 16682)
-- Name: order fk_order_tariff; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."order"
    ADD CONSTRAINT fk_order_tariff FOREIGN KEY (id_tariff) REFERENCES public.tariffs(id);


--
-- TOC entry 3313 (class 2606 OID 16587)
-- Name: hosts fk_server_tariff; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hosts
    ADD CONSTRAINT fk_server_tariff FOREIGN KEY (id_tariff) REFERENCES public.tariffs(id) NOT VALID;


--
-- TOC entry 3316 (class 2606 OID 16645)
-- Name: host_sharings fk_share_client; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.host_sharings
    ADD CONSTRAINT fk_share_client FOREIGN KEY (id_host) REFERENCES public.hosts(id);


--
-- TOC entry 3317 (class 2606 OID 16650)
-- Name: host_sharings fk_share_host; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.host_sharings
    ADD CONSTRAINT fk_share_host FOREIGN KEY (id_client) REFERENCES public.clients(id);


--
-- TOC entry 3314 (class 2606 OID 16592)
-- Name: hosts fk_user_server; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.hosts
    ADD CONSTRAINT fk_user_server FOREIGN KEY (id_client) REFERENCES public.clients(id) NOT VALID;


--
-- TOC entry 3315 (class 2606 OID 16635)
-- Name: versions fk_version_game; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.versions
    ADD CONSTRAINT fk_version_game FOREIGN KEY (id_game) REFERENCES public.games(id) NOT VALID;


--
-- TOC entry 3321 (class 2606 OID 17379)
-- Name: order order_id_host_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."order"
    ADD CONSTRAINT order_id_host_fkey FOREIGN KEY (id_host) REFERENCES public.hosts(id);


-- Completed on 2025-05-26 22:42:13 +07

--
-- PostgreSQL database dump complete
--

