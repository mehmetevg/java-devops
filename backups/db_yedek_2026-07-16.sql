--
-- PostgreSQL database dump
--

\restrict 0TJj3MKCI77Y82kMCmKb4B7uJ2VGCu7EKpZoMjGzrdXFBrc9wwgGLJnkTCLwzYu

-- Dumped from database version 15.18
-- Dumped by pg_dump version 15.18

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
-- Name: maclar; Type: TABLE; Schema: public; Owner: stajuser
--

CREATE TABLE public.maclar (
    id integer NOT NULL,
    tarih character varying(50),
    ev_sahibi character varying(50),
    skor character varying(20),
    deplasman character varying(50),
    sari_kart character varying(20),
    kirmizi_kart character varying(20)
);


ALTER TABLE public.maclar OWNER TO stajuser;

--
-- Name: maclar_id_seq; Type: SEQUENCE; Schema: public; Owner: stajuser
--

CREATE SEQUENCE public.maclar_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.maclar_id_seq OWNER TO stajuser;

--
-- Name: maclar_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: stajuser
--

ALTER SEQUENCE public.maclar_id_seq OWNED BY public.maclar.id;


--
-- Name: maclar id; Type: DEFAULT; Schema: public; Owner: stajuser
--

ALTER TABLE ONLY public.maclar ALTER COLUMN id SET DEFAULT nextval('public.maclar_id_seq'::regclass);


--
-- Data for Name: maclar; Type: TABLE DATA; Schema: public; Owner: stajuser
--

COPY public.maclar (id, tarih, ev_sahibi, skor, deplasman, sari_kart, kirmizi_kart) FROM stdin;
92	05.04.2026	Keşan Spor	3-2	Ata Gençspor	10	0
93	12.04.2026	İpsalaspor	1-3	Kırcasalihspor	2	0
94	03.05.2026	Tepecikspor	1-0	Edirne 1922 FK	4	1
95	28.06.2026	İstasyon Gençlikspor	0-1	Havsaspor	6	1
\.


--
-- Name: maclar_id_seq; Type: SEQUENCE SET; Schema: public; Owner: stajuser
--

SELECT pg_catalog.setval('public.maclar_id_seq', 95, true);


--
-- Name: maclar maclar_pkey; Type: CONSTRAINT; Schema: public; Owner: stajuser
--

ALTER TABLE ONLY public.maclar
    ADD CONSTRAINT maclar_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

\unrestrict 0TJj3MKCI77Y82kMCmKb4B7uJ2VGCu7EKpZoMjGzrdXFBrc9wwgGLJnkTCLwzYu

