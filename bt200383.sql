use [bt200383]
go


CREATE TABLE [Administrator]
( 
	[korisnickoIme]      varchar(20)  NOT NULL 
)
go

ALTER TABLE [Administrator]
	ADD CONSTRAINT [XPKAdministrator] PRIMARY KEY  CLUSTERED ([korisnickoIme] ASC)
go

CREATE TABLE [Grad]
( 
	[idGrad]             integer  NOT NULL  IDENTITY ,
	[naziv]              varchar(20)  NULL ,
	[postBroj]           varchar(20)  NULL 
)
go

ALTER TABLE [Grad]
	ADD CONSTRAINT [XPKGrad] PRIMARY KEY  CLUSTERED ([idGrad] ASC)
go

CREATE TABLE [Korisnik]
( 
	[korisnickoIme]      varchar(20)  NOT NULL ,
	[ime]                varchar(20)  NULL ,
	[prezime]            varchar(20)  NULL ,
	[sifra]              varchar(20)  NULL ,
	[brojPoslatihPaketa] integer  NULL 
	CONSTRAINT [Nula_5]
		 DEFAULT  0
)
go

ALTER TABLE [Korisnik]
	ADD CONSTRAINT [XPKKorisnik] PRIMARY KEY  CLUSTERED ([korisnickoIme] ASC)
go

CREATE TABLE [Kurir]
( 
	[korisnickoIme]      varchar(20)  NOT NULL ,
	[regBroj]            varchar(20)  NOT NULL ,
	[brojIsporucenihPaketa] integer  NULL 
	CONSTRAINT [Nula_3]
		 DEFAULT  0,
	[profit]             decimal(10,3)  NULL 
	CONSTRAINT [Nula_1]
		 DEFAULT  0,
	[status]             integer  NULL 
	CONSTRAINT [Nula_2]
		 DEFAULT  0
	CONSTRAINT [Status_kurira_1]
		CHECK  ( [status]=0 OR [status]=1 )
)
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [XPKKurir] PRIMARY KEY  CLUSTERED ([korisnickoIme] ASC)
go

CREATE TABLE [Opstina]
( 
	[idOpstina]          integer  NOT NULL  IDENTITY ,
	[naziv]              varchar(20)  NULL ,
	[x]                  integer  NULL ,
	[y]                  integer  NULL ,
	[idGrad]             integer  NOT NULL 
)
go

ALTER TABLE [Opstina]
	ADD CONSTRAINT [XPKOpstina] PRIMARY KEY  CLUSTERED ([idOpstina] ASC)
go

CREATE TABLE [Paket]
( 
	[idPaket]            integer  NOT NULL  IDENTITY ,
	[tip]                integer  NULL 
	CONSTRAINT [Tip_paketa_1]
		CHECK  ( [tip]=0 OR [tip]=1 OR [tip]=2 ),
	[tezina]             decimal(10,3)  NULL ,
	[idOpstinaDostavi]   integer  NOT NULL ,
	[idOpstinaPreuzmi]   integer  NOT NULL ,
	[statusIsporuke]     integer  NULL 
	CONSTRAINT [Nula_4]
		 DEFAULT  0
	CONSTRAINT [Status_paketa_1]
		CHECK  ( [statusIsporuke]=0 OR [statusIsporuke]=1 OR [statusIsporuke]=2 OR [statusIsporuke]=3 ),
	[cena]               decimal(10,3)  NULL ,
	[vremePrihvatanjaZahteva] datetime  NULL ,
	[korisnickoIme]      varchar(20)  NOT NULL ,
	[kurir]              varchar(20)  NULL ,
	[procenat]           decimal(10,3)  NULL 
)
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [XPKPaket] PRIMARY KEY  CLUSTERED ([idPaket] ASC)
go

CREATE TABLE [Ponuda]
( 
	[idPonuda]           integer  NOT NULL  IDENTITY ,
	[procenatCene]       decimal(10,3)  NULL ,
	[korisnickoIme]      varchar(20)  NOT NULL ,
	[idPaket]            integer  NOT NULL 
)
go

ALTER TABLE [Ponuda]
	ADD CONSTRAINT [XPKPonuda] PRIMARY KEY  CLUSTERED ([idPonuda] ASC)
go

CREATE TABLE [Vozilo]
( 
	[regBroj]            varchar(20)  NOT NULL ,
	[potrosnja]          decimal(10,3)  NULL ,
	[tipGoriva]          integer  NULL 
)
go

ALTER TABLE [Vozilo]
	ADD CONSTRAINT [XPKVozilo] PRIMARY KEY  CLUSTERED ([regBroj] ASC)
go

CREATE TABLE [Zahtev]
( 
	[korisnickoIme]      varchar(20)  NOT NULL ,
	[regBroj]            varchar(20)  NOT NULL 
)
go

ALTER TABLE [Zahtev]
	ADD CONSTRAINT [XPKZahtev] PRIMARY KEY  CLUSTERED ([korisnickoIme] ASC)
go


ALTER TABLE [Administrator]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([korisnickoIme]) REFERENCES [Korisnik]([korisnickoIme])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Kurir]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([korisnickoIme]) REFERENCES [Korisnik]([korisnickoIme])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([regBroj]) REFERENCES [Vozilo]([regBroj])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Opstina]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([idGrad]) REFERENCES [Grad]([idGrad])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Paket]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([idOpstinaDostavi]) REFERENCES [Opstina]([idOpstina])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([idOpstinaPreuzmi]) REFERENCES [Opstina]([idOpstina])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([korisnickoIme]) REFERENCES [Korisnik]([korisnickoIme])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_15] FOREIGN KEY ([kurir]) REFERENCES [Korisnik]([korisnickoIme])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Ponuda]
	ADD CONSTRAINT [R_10] FOREIGN KEY ([korisnickoIme]) REFERENCES [Kurir]([korisnickoIme])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Ponuda]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([idPaket]) REFERENCES [Paket]([idPaket])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Zahtev]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([korisnickoIme]) REFERENCES [Korisnik]([korisnickoIme])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Zahtev]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([regBroj]) REFERENCES [Vozilo]([regBroj])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

USE [bt200383]
GO

CREATE PROCEDURE [dbo].[SP_EraseAll]
AS
BEGIN
	delete from Ponuda
	delete from Paket
	delete from Opstina
	delete from Grad
	delete from Administrator
    delete from Zahtev
    delete from Kurir
	delete from Vozilo
	delete from Korisnik
END
GO

CREATE PROCEDURE [dbo].[SP_GrantRequest]
@korisnickoIme varchar(20)
AS
BEGIN
	if not exists (select * from Zahtev where korisnickoIme=@korisnickoIme)
		return 0

	declare @regBroj varchar(20)
	select @regBroj=regBroj from Zahtev where korisnickoIme=@korisnickoIme

	begin transaction T1
	begin try

		insert into Kurir(korisnickoIme, regBroj) values (@korisnickoIme, @regBroj)

		delete from Zahtev where korisnickoIme = @korisnickoIme

	commit transaction
	end try

	begin catch
		rollback transaction T1
		return 0
	end catch

	return 1

END
GO


USE [bt200383]
GO

CREATE PROCEDURE [dbo].[SP_InsertPackage]
@distFrom int, @distTo int, @userName varchar(20), @packageType int, @weight decimal(10,3)
AS
BEGIN
	if not exists(select * from Opstina where idOpstina=@distFrom) return -1
	if not exists(select * from Opstina where idOpstina=@distTo) return -1
	if not exists(select * from Korisnik where korisnickoIme=@userName) return -1

	begin transaction T2
	begin try
		insert into Paket(idOpstinaPreuzmi, idOpstinaDostavi, korisnickoIme, tip, tezina) values (@distFrom, @distTo, @userName, @packageType, @weight)
		update Korisnik set brojPoslatihPaketa = brojPoslatihPaketa + 1 where korisnickoIme = @username
	commit transaction T2
	end try

	begin catch
		rollback transaction T2
		return -1
	end catch

	return SCOPE_IDENTITY() 
END
GO


CREATE PROCEDURE [dbo].[SP_InsertTranspOffer]
@kurirKorIme varchar(20), @idPaketa int, @procenat decimal(10,3)
AS
BEGIN
	if not exists(select * from Kurir where korisnickoIme=@kurirKorIme and status=0) return -1
	if not exists(select * from Paket where idPaket=@idPaketa) return -1

	begin transaction T3
	begin try
		insert into Ponuda(idPaket, korisnickoIme, procenatCene) values (@idPaketa, @kurirKorIme, @procenat)
		
	commit transaction T3
	end try

	begin catch
		rollback transaction T3
		return -1
	end catch

	return SCOPE_IDENTITY() 
END
GO


CREATE trigger [dbo].[TR_TransportOffer]
on [dbo].[Paket]
FOR INSERT, UPDATE
AS
BEGIN
	declare @kursor cursor
	declare @idPak int, @tip int, @idOpst1 int, @idOpst2 int, @st int
	declare @tezina decimal(10,3), @procenat decimal(10,3)
	declare @vreme datetime

	set @kursor = cursor for
	select idPaket
	from inserted

	open @kursor

	fetch from @kursor
	into @idPak


	while @@FETCH_STATUS=0
	begin
		select @tip = tip, @tezina = tezina, @procenat = procenat, @idOpst1 = idOpstinaPreuzmi, @idOpst2 =  idOpstinaDostavi, @st = statusIsporuke
		from Paket

		if(@procenat IS NULL and @st = 0) --or @idKurir = null or @procenat = null)  --kreiran paket
		begin
			declare @osnovna_cena decimal(10,3)
			declare @faktor int, @cena_po_kg int
			if(@tip = 0) begin
				set @osnovna_cena = 10
				set @faktor = 0
				set @cena_po_kg = 0
			end
			else if(@tip = 1) begin
				set @osnovna_cena = 25
				set @faktor = 1
				set @cena_po_kg = 100
			end
			else begin
				set @osnovna_cena = 75
				set @faktor = 2
				set @cena_po_kg = 300
			end

			declare @x1 int, @y1 int, @x2 int, @y2 int
			select @x1 = x, @y1 = y from Opstina where idOpstina = @idOpst1
			select @x2 = x, @y2 = y from Opstina where idOpstina = @idOpst2

			declare @dist decimal(38,8)
			set @dist = SQRT(SQUARE(@x1 - @x2) + SQUARE(@y1 - @y2))

			update Paket set cena = (@osnovna_cena+(@faktor * @tezina)*@cena_po_kg)*@dist
			where idPaket=@idPak
		end
		else if(@procenat is not null and @st=0) --prihvacena ponuda
		begin
			delete from Ponuda where idPaket = @idPak

			update Paket set cena = cena + cena*@procenat/100, statusIsporuke = 1
			where idPaket=@idPak
		end

		fetch from @kursor
		into @idPak
	end

	close @kursor
	deallocate @kursor
END
GO



