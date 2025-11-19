import java.io.*;
import java.util.*;

public class LibrarySystem {
    static class Book {
        int bookId;
        String title;
        String author;
        String category;
        boolean isIssued;
        Book(int id, String t, String a, String c) {
            bookId = id;
            title = t;
            author = a;
            category = c;
            isIssued = false;
        }
        void display() {
            System.out.println(bookId + " | " + title + " | " + author +
                    " | " + category + " | Issued: " + isIssued);
        }
        public String toFile() {
            return bookId + "," + title + "," + author + "," + category + "," + isIssued;
        }
        public static Book fromFile(String line) {
            String[] p = line.split(",");
            Book b = new Book(Integer.parseInt(p[0]), p[1], p[2], p[3]);
            b.isIssued = Boolean.parseBoolean(p[4]);
            return b;
        }
    }
    static class Member {
        int memberId;
        String name;
        String email;
        List<Integer> issued = new ArrayList<>();
        Member(int id, String n, String e) {
            memberId = id;
            name = n;
            email = e;
        }
        void display() {
            System.out.println(memberId + " | " + name + " | " + email +
                    " | Books: " + issued);
        }
        public String toFile() {
            return memberId + "," + name + "," + email + "," + issued.toString();
        }
        public static Member fromFile(String line) {
            String[] p = line.split(",", 4);
            Member m = new Member(Integer.parseInt(p[0]), p[1], p[2]);
            return m;
        }
    }
    static class LibraryManager {

        Map<Integer, Book> books = new HashMap<>();
        Map<Integer, Member> members = new HashMap<>();

        File bookFile = new File("books.txt");
        File memberFile = new File("members.txt");

        LibraryManager() {
            loadData();
        }

        // Add book
        void addBook(String title, String author, String category) {
            int id = books.size() + 1;
            books.put(id, new Book(id, title, author, category));
            saveData();
            System.out.println("Book added with ID: " + id);
        }
        void addMember(String name, String email) {
            int id = members.size() + 1;
            members.put(id, new Member(id, name, email));
            saveData();
            System.out.println("Member added with ID: " + id);
        }
        void issueBook(int bookId, int memberId) {
            Book b = books.get(bookId);
            Member m = members.get(memberId);
            if (b == null || m == null) {
                System.out.println("Invalid ID.");
                return;
            }
            if (b.isIssued) {
                System.out.println("Book already issued.");
                return;
            }
            b.isIssued = true;
            m.issued.add(bookId);
            saveData();
            System.out.println("Book issued.");
        }
        void returnBook(int bookId, int memberId) {
            Book b = books.get(bookId);
            Member m = members.get(memberId);

            if (b == null || m == null) {
                System.out.println("Invalid ID.");
                return;
            }
            b.isIssued = false;
            m.issued.remove(Integer.valueOf(bookId));
            saveData();
            System.out.println("Book returned.");
        }
        void search(String key) {
            for (Book b : books.values()) {
                if (b.title.contains(key) || 
                    b.author.contains(key) || 
                    b.category.contains(key)) 
                {
                    b.display();
                }
            }
        }
        void sortBooks() {
            List<Book> list = new ArrayList<>(books.values());
            Collections.sort(list, (a, b) -> a.title.compareTo(b.title));
            for (Book b : list) b.display();
        }
        void loadData() {
            try {
                if (!bookFile.exists()) bookFile.createNewFile();
                if (!memberFile.exists()) memberFile.createNewFile();
                BufferedReader br = new BufferedReader(new FileReader(bookFile));
                String line;
                while ((line = br.readLine()) != null) {
                    Book b = Book.fromFile(line);
                    books.put(b.bookId, b);
                }
                br.close();
                br = new BufferedReader(new FileReader(memberFile));
                while ((line = br.readLine()) != null) {
                    Member m = Member.fromFile(line);
                    members.put(m.memberId, m);
                }
                br.close();
            } catch (Exception ignored) {}
        }
        void saveData() {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(bookFile));
                for (Book b : books.values()) {
                    bw.write(b.toFile());
                    bw.newLine();
                }
                bw.close();
                bw = new BufferedWriter(new FileWriter(memberFile));
                for (Member m : members.values()) {
                    bw.write(m.toFile());
                    bw.newLine();
                }
                bw.close();
            } catch (Exception ignored) {}
        }
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LibraryManager lib = new LibraryManager();
        while (true) {
            System.out.println("\n1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search");
            System.out.println("6. Sort Books");
            System.out.println("7. Exit");
            System.out.print("Choice: ");
            int ch = sc.nextInt();
            sc.nextLine();
            switch (ch) {
                case 1:
                    System.out.print("Title: ");
                    String t = sc.nextLine();
                    System.out.print("Author: ");
                    String a = sc.nextLine();
                    System.out.print("Category: ");
                    String c = sc.nextLine();
                    lib.addBook(t, a, c);
                    break;
                case 2:
                    System.out.print("Name: ");
                    String n = sc.nextLine();
                    System.out.print("Email: ");
                    String e = sc.nextLine();
                    lib.addMember(n, e);
                    break;
                case 3:
                    System.out.print("Book ID: ");
                    int b = sc.nextInt();
                    System.out.print("Member ID: ");
                    int m = sc.nextInt();
                    lib.issueBook(b, m);
                    break;
                case 4:
                    System.out.print("Book ID: ");
                    int rb = sc.nextInt();
                    System.out.print("Member ID: ");
                    int rm = sc.nextInt();
                    lib.returnBook(rb, rm);
                    break;
                case 5:
                    System.out.print("Keyword: ");
                    String k = sc.nextLine();
                    lib.search(k);
                    break;
                case 6:
                    lib.sortBooks();
                    break;
                case 7:
                    System.out.println("Exiting...");
                    return;
            }
        }
    }
}
