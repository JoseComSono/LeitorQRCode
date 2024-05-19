PGDMP  	                    |           QRCode    16.2    16.2     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �           1262    17667    QRCode    DATABASE     �   CREATE DATABASE "QRCode" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Portuguese_Portugal.1252';
    DROP DATABASE "QRCode";
                postgres    false            �            1255    17735 
   atualiza()    FUNCTION       CREATE FUNCTION public.atualiza() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE categorias
        SET total_despesas = total_despesas + NEW.valor
        WHERE categorias.id = NEW.id_categoria;
    END IF;
    RETURN NEW;
END;
$$;
 !   DROP FUNCTION public.atualiza();
       public          postgres    false            �            1255    17719    atualiza_despesas_categoria()    FUNCTION     +  CREATE FUNCTION public.atualiza_despesas_categoria() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE categorias
        SET total_despesas = total_despesas + NEW.valor_com_iva
        WHERE id = NEW.id_categoria;
    END IF;
    RETURN NEW;
END;
$$;
 4   DROP FUNCTION public.atualiza_despesas_categoria();
       public          postgres    false            �            1259    17700 
   categorias    TABLE     �   CREATE TABLE public.categorias (
    id integer NOT NULL,
    nome character varying(100),
    total_despesas numeric(10,2) DEFAULT 0,
    teto_despesa numeric(10,2),
    CONSTRAINT teto_despesa_check CHECK ((total_despesas <= teto_despesa))
);
    DROP TABLE public.categorias;
       public         heap    postgres    false            �            1259    17699    categorias_id_seq    SEQUENCE     �   CREATE SEQUENCE public.categorias_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 (   DROP SEQUENCE public.categorias_id_seq;
       public          postgres    false    216            �           0    0    categorias_id_seq    SEQUENCE OWNED BY     G   ALTER SEQUENCE public.categorias_id_seq OWNED BY public.categorias.id;
          public          postgres    false    215            �            1259    17722    fatura    TABLE     G  CREATE TABLE public.fatura (
    id integer NOT NULL,
    id_categoria integer,
    valor numeric,
    nif_emitente character varying(100),
    nif_adquirente character varying(100),
    pais character varying(100),
    data character varying(100),
    identificacao character varying(100),
    atcud character varying(100)
);
    DROP TABLE public.fatura;
       public         heap    postgres    false            �            1259    17721    fatura_id_seq    SEQUENCE     �   CREATE SEQUENCE public.fatura_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 $   DROP SEQUENCE public.fatura_id_seq;
       public          postgres    false    218            �           0    0    fatura_id_seq    SEQUENCE OWNED BY     ?   ALTER SEQUENCE public.fatura_id_seq OWNED BY public.fatura.id;
          public          postgres    false    217            W           2604    17782    categorias id    DEFAULT     n   ALTER TABLE ONLY public.categorias ALTER COLUMN id SET DEFAULT nextval('public.categorias_id_seq'::regclass);
 <   ALTER TABLE public.categorias ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    216    215    216            Y           2604    17783 	   fatura id    DEFAULT     f   ALTER TABLE ONLY public.fatura ALTER COLUMN id SET DEFAULT nextval('public.fatura_id_seq'::regclass);
 8   ALTER TABLE public.fatura ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    218    217    218            �          0    17700 
   categorias 
   TABLE DATA           L   COPY public.categorias (id, nome, total_despesas, teto_despesa) FROM stdin;
    public          postgres    false    216   �       �          0    17722    fatura 
   TABLE DATA           y   COPY public.fatura (id, id_categoria, valor, nif_emitente, nif_adquirente, pais, data, identificacao, atcud) FROM stdin;
    public          postgres    false    218   2       �           0    0    categorias_id_seq    SEQUENCE SET     ?   SELECT pg_catalog.setval('public.categorias_id_seq', 3, true);
          public          postgres    false    215            �           0    0    fatura_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.fatura_id_seq', 9, true);
          public          postgres    false    217            \           2606    17706    categorias categorias_pkey 
   CONSTRAINT     X   ALTER TABLE ONLY public.categorias
    ADD CONSTRAINT categorias_pkey PRIMARY KEY (id);
 D   ALTER TABLE ONLY public.categorias DROP CONSTRAINT categorias_pkey;
       public            postgres    false    216            ^           2606    17729    fatura fatura_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.fatura
    ADD CONSTRAINT fatura_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.fatura DROP CONSTRAINT fatura_pkey;
       public            postgres    false    218            `           2620    17736    fatura atualizacao_despesas    TRIGGER     s   CREATE TRIGGER atualizacao_despesas AFTER INSERT ON public.fatura FOR EACH ROW EXECUTE FUNCTION public.atualiza();
 4   DROP TRIGGER atualizacao_despesas ON public.fatura;
       public          postgres    false    220    218            _           2606    17730    fatura fatura_id_categoria_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.fatura
    ADD CONSTRAINT fatura_id_categoria_fkey FOREIGN KEY (id_categoria) REFERENCES public.categorias(id);
 I   ALTER TABLE ONLY public.fatura DROP CONSTRAINT fatura_id_categoria_fkey;
       public          postgres    false    4700    218    216            �   ;   x�3�.-H-�M-JNL��4�30�45 Q\Ɯ��%�)��(�F����w�����qqq �a      �   G   x���4�412�4426153��䴄΀N#CK��!�[�����o``l����fb��q��qqq �$     