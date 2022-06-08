import entity.AddressInfoEntity;
import entity.PersonEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main
{


    private static EntityManagerFactory fac = Persistence.createEntityManagerFactory("default");
    private static EntityManager man = fac.createEntityManager();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args)
    {

        EntityTransaction tr = man.getTransaction();
        Session s = man.unwrap(org.hibernate.Session.class);
        Transaction tr1 = s.getTransaction();

        //Dodawanie osóby
        try
        {
            tr.begin();
            System.out.println("-START-Dodawanie osóby-");
            PersonEntity major = new PersonEntity();
            System.out.print("Wpisz imie: ");
            major.setpName(sc.nextLine());
            System.out.print("Wpisz nazwisko: ");
            major.setpSurname(sc.nextLine());
            System.out.print("Wpisz email: ");
            major.setpEmail(sc.nextLine());
            System.out.println("Ile adresow chcesz wpisać?");
            int amount = sc.nextInt();
            sc.nextLine();
            major.setAddressInfosByPId(new ArrayList<AddressInfoEntity>());
            for (int i = 0; i < amount; i++)
            {
                System.out.printf("Wprowadź " + (i + 1) + " adres:");
                System.out.println();
                AddressInfoEntity address = new AddressInfoEntity();
                System.out.print("Wpisz ulice: ");
                address.setStreet(sc.nextLine());
                System.out.print("Wpisz numer domu: ");
                address.setHouseNumber(sc.nextLine());
                System.out.print("Wpisz miasto: ");
                address.setTown(sc.nextLine());
                System.out.print("Wpisz wojewodztwo: ");
                address.setState(sc.nextLine());
                System.out.print("Wpisz panstwo: ");
                address.setCountry(sc.nextLine());
                major.getAddressInfosByPId().add(address);
                address.setPersonByAiPId(major);

                System.out.println("Dodano adres...");
                System.out.println();
            }
            man.persist(major);
            System.out.println("-KONIEC-Dodawanie osóby-\n");
            tr.commit();
        } finally
        {
            if (tr.isActive())
                tr.rollback();
        }

        try
        {
            System.out.println("-START-Odczyt osoby-");
            tr.begin();
            TypedQuery<PersonEntity> personBySth = wybieranieFiltra();
            personBySth.setParameter(1, sc.nextLine());
            int k = 0;
            for (PersonEntity p : personBySth.getResultList())
            {
                System.out.println(p);
                k++;
            }
            if (k == 0)
                System.out.println("Brak osób pasujacych do filtra");
            System.out.println("-KONIEC-Odczyt osóby-\n");
            tr.commit();
        } finally
        {
            if (tr.isActive())
                tr.rollback();
        }

        try
        {
            System.out.println("-START-Modyfikacja osóby-");
            tr1.begin();
            TypedQuery<PersonEntity> personBySth = wybieranieFiltra();
            personBySth.setParameter(1, sc.nextLine());
            List<PersonEntity> lista = personBySth.getResultList();

            int k = 0;
            System.out.println("Wybierz osóbe do zmiany: ");
            for (PersonEntity p : personBySth.getResultList())
            {
                System.out.print(k + ": \n");
                System.out.println(p);
                k++;
            }
            if (k == 0)
                System.out.println("Brak osób pasujacych do filtra");
            int g;
            do
            {
                System.out.print("Wpisz numer osoby: ");
                g = sc.nextInt();
                sc.nextLine();
            } while (g < 0 || g >= k);
            PersonEntity p = lista.get(g);
            System.out.print("Wpisz nowe imie: ");
            p.setpName(sc.nextLine());
            System.out.print("Wpisz nowe nazwisko: ");
            p.setpSurname(sc.nextLine());
            System.out.print("Wpisz nowy email: ");
            p.setpEmail(sc.nextLine());

            int i = 1;
            for (AddressInfoEntity address : p.getAddressInfosByPId())
            {
                System.out.printf("Wpisz " + i +" adres:");
                System.out.println();
                System.out.print("Wpisz ulice: ");
                address.setStreet(sc.nextLine());
                System.out.print("Wpisz numer domu: ");
                address.setHouseNumber(sc.nextLine());
                System.out.print("Wpisz miasto: ");
                address.setTown(sc.nextLine());
                System.out.print("Wpisz wojewodztwo: ");
                address.setState(sc.nextLine());
                System.out.print("Wpisz panstwo: ");
                address.setCountry(sc.nextLine());
                i++;
            }
            s.update(p);
            System.out.println("-KONIEC-Modyfikacja osóby-");
            tr1.commit();
        } finally
        {
            if (tr1.isActive())
                tr1.rollback();
        }
        try
        {
            System.out.println("-START-Usuwanie osóby-");
            tr1.begin();
            TypedQuery<PersonEntity> personBySth = wybieranieFiltra();
            personBySth.setParameter(1, sc.nextLine());

            List<PersonEntity> lista = personBySth.getResultList();
            int k = 0;
            System.out.println("Wybierz osóbe do usuniecia: ");
            for (PersonEntity p : lista)
            {
                System.out.print(k + ": \n");
                System.out.println(p);
                k++;
            }
            if (k == 0)
                System.out.println("Brak osób pasujacych do filtra");

            int g;
            do
            {
                System.out.print("Wpisz numer osóby: ");
                g = sc.nextInt();
                sc.nextLine();
            } while (g < 0 || g >= k);
            PersonEntity p = lista.get(g);

            man.remove(p);
            man.flush();
            man.clear();
            System.out.println("Usunieto pomyslnie...");
            System.out.println("-KONIEC-Usuwanie osóby-");
            tr1.commit();
        } finally
        {
            if (tr1.isActive())
                tr1.rollback();
        }

    }

    private static TypedQuery<PersonEntity> wybieranieFiltra()
    {
        System.out.println("FILTROWANIE PO: ");
        System.out.println("1. Imieniu");
        System.out.println("2. Nazwisku");
        System.out.println("3. Województwie");
        System.out.println("4. Kraju");
        System.out.println("5. Mieście");
        System.out.println("6. Mailu\n");
        System.out.print("Wybierz: ");
        int option = sc.nextInt();
        sc.nextLine();
        while (true)
        {
            switch (option)
            {
                case 1:
                    System.out.print("Wprowadź imie: ");
                    return man.createNamedQuery("PersonEntity.ByName", PersonEntity.class);
                case 2:
                    System.out.print("Wwprowadź nazwisko: ");
                    return man.createNamedQuery("PersonEntity.BySurname", PersonEntity.class);
                case 3:
                    System.out.print("Wprowadź województwo: ");
                    return man.createNamedQuery("PersonEntity.ByState", PersonEntity.class);
                case 4:
                    System.out.print("Wprowaź kraj: ");
                    return man.createNamedQuery("PersonEntity.ByCountry", PersonEntity.class);
                case 5:
                    System.out.print("Wprowadź miasto: ");
                    return man.createNamedQuery("PersonEntity.ByTown", PersonEntity.class);
                case 6:
                    System.out.print("Wprowadź maila: ");
                    return man.createNamedQuery("PersonEntity.ByEmail", PersonEntity.class);
                default:
                    System.out.println("Nie ma takiej opcji!");
                    System.exit(1);
            }
        }
    }
}
