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



