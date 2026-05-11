# LunaCaffe Point of Sales System

## Dokumentasi Akademik Tugas Pemrograman Berorientasi Objek

LunaCaffe POS adalah aplikasi desktop berbasis Java Swing yang dikembangkan sebagai studi kasus penerapan Pemrograman Berorientasi Objek pada sistem operasional kafe. Sistem ini mendukung pemesanan mandiri pelanggan, pemantauan antrean pesanan, pengelolaan produk, pengelolaan akun pegawai, dan rekapitulasi transaksi harian dengan penyimpanan lokal SQLite.

## Identitas Proyek

| Komponen | Keterangan |
| --- | --- |
| Nama Sistem | LunaCaffe Point of Sales System |
| Jenis Aplikasi | Desktop POS berbasis Java |
| Mata Kuliah | Pemrograman Berorientasi Objek |
| Bahasa Pemrograman | Java |
| Antarmuka | Java Swing dengan FlatLaf |
| Database | SQLite |
| Arsitektur | MVC dan DAO |
| File Dokumentasi | `index.html` |
| File Demo Visual | `demo.html` |

## Tim Pengembang

- Firgiawan Listianto
- Muh. Nabil Makarimsyah
- Jumaria
- Kelvin Surya Putra

## Abstrak

LunaCaffe POS dirancang untuk membantu proses transaksi pada kafe melalui dua area utama, yaitu antarmuka pelanggan dan dashboard pegawai. Pelanggan dapat memilih menu, mengelola keranjang, melakukan checkout, dan memperoleh nomor antrean. Kasir dapat memantau pesanan serta memperbarui status pesanan. Admin dapat mengelola produk, stok, harga, gambar menu, akun pegawai, dan laporan transaksi. Sistem ini dibuat dengan pendekatan objek agar setiap entitas bisnis direpresentasikan secara jelas dalam class Java.

## Latar Belakang

Operasional kafe membutuhkan pengelolaan data menu, stok, pesanan, dan transaksi secara konsisten. Pencatatan manual berpotensi menyebabkan kesalahan pada perhitungan total, stok, maupun status pesanan. Oleh karena itu, proyek ini menggunakan studi kasus POS untuk menunjukkan bagaimana konsep PBO dapat diterapkan pada aplikasi yang memiliki kebutuhan nyata.

## Tujuan

1. Membangun aplikasi POS desktop yang dapat digunakan secara lokal.
2. Menerapkan konsep class, object, encapsulation, inheritance, polymorphism, dan abstraction.
3. Memisahkan tanggung jawab program melalui model, view, DAO, dan utility.
4. Menggunakan SQLite sebagai penyimpanan data transaksi dan master data.
5. Menyediakan dokumentasi akademik yang menjelaskan rancangan dan implementasi sistem.

## Ruang Lingkup Sistem

### Modul Pelanggan

- Menampilkan katalog produk dengan gambar lokal.
- Menyediakan pencarian menu secara langsung.
- Menyediakan filter kategori menu.
- Menyediakan pengurutan berdasarkan nama dan harga.
- Mengelola keranjang dengan tombol tambah, kurang, dan hapus item.
- Melakukan validasi stok sebelum checkout.
- Menghasilkan struk dan nomor antrean.

### Modul Kasir

- Login menggunakan akun pegawai.
- Melihat daftar pesanan masuk.
- Menandai pesanan sebagai selesai.
- Mengakses rekapitulasi harian sesuai kebutuhan operasional.

### Modul Admin

- Menambah produk menu.
- Mengubah data produk.
- Menghapus produk.
- Mengubah harga dan stok produk.
- Menentukan gambar produk dari resource lokal atau path file.
- Mengatur label produk `NEW` dan `BEST`.
- Menambah dan menghapus akun pegawai.
- Mengekspor rekap transaksi harian ke CSV.

## Akun Uji

| Role | Username | Password | Hak Akses |
| --- | --- | --- | --- |
| Admin | `admin` | `123` | Semua fitur dashboard |
| Kasir | `kasir1` | `123` | Pesanan live dan rekap |
| Pelanggan | Tidak perlu login | Tidak perlu login | Katalog, keranjang, checkout |

## Arsitektur Sistem

Sistem menggunakan pendekatan MVC dengan lapisan DAO untuk menjaga pemisahan tanggung jawab.

| Lapisan | File Utama | Tanggung Jawab |
| --- | --- | --- |
| Model | `Menu`, `Pesanan`, `DetailPesanan`, `Aktor`, `Pegawai`, `Admin`, `Kasir`, `Pembeli` | Merepresentasikan objek domain |
| View | `MainFrame`, `CustomerPanel`, `LoginPanel`, `DashboardPanel` | Menampilkan antarmuka pengguna |
| DAO | `MenuDAO`, `OrderDAO`, `UserDAO` | Mengakses dan memanipulasi data SQLite |
| Utility | `DatabaseConnection`, `DatabaseSeeder`, `SessionManager` | Koneksi database, data awal, dan sesi login |

## Struktur Direktori

```text
LunaCaffe
├── src/main/java/com/lunacaffe
│   ├── Main.java
│   ├── dao
│   ├── model
│   ├── util
│   └── view
├── src/main/resources/images/menu
├── lunacaffe.db
├── pom.xml
├── index.html
├── demo.html
└── README.md
```

## Desain Database

Database menggunakan file lokal `lunacaffe.db`.

| Tabel | Fungsi | Kolom Penting |
| --- | --- | --- |
| `users` | Menyimpan akun pegawai | `id`, `nama`, `username`, `password`, `role` |
| `menus` | Menyimpan data produk menu | `id`, `nama`, `kategori`, `harga`, `stok`, `image_path`, `is_new`, `is_bestseller` |
| `orders` | Menyimpan transaksi utama | `id`, `nama_pelanggan`, `total_harga`, `status`, `created_at` |
| `order_details` | Menyimpan item pada transaksi | `order_id`, `menu_id`, `qty`, `subtotal` |

Database dapat diakses melalui panel admin aplikasi. Untuk pemeriksaan manual, gunakan DB Browser for SQLite dan buka file `lunacaffe.db`.

## Penerapan Konsep PBO

### Class dan Object

Class digunakan sebagai cetak biru data, sedangkan object adalah instansiasi yang digunakan saat program berjalan.

```java
Menu menu = new Menu(id, nama, kategori, harga, stok, imagePath, isNew, isBest);
Pesanan pesanan = new Pesanan("TEMP", "Guest", "");
```

### Encapsulation

Atribut penting pada model dibuat private dan diakses melalui method agar perubahan data lebih terkendali.

```java
private double harga;

public double getHarga() {
    return harga;
}
```

### Inheritance

Class `Admin` dan `Kasir` mewarisi atribut serta perilaku umum dari `Pegawai`.

```java
public class Admin extends Pegawai {
    public Admin(String nama, String username, String password) {
        super(null, nama, username, password, "admin");
    }
}
```

### Abstraction

Class `Aktor` menjadi bentuk umum dari aktor sistem dan menetapkan kontrak method yang harus diimplementasikan oleh turunannya.

```java
public abstract class Aktor {
    public abstract void masukSistem();
}
```

### Polymorphism

Method yang sama dapat memiliki implementasi berbeda pada class turunan, misalnya cara masuk sistem antara pelanggan dan pegawai.

## Alur Penggunaan

### Alur Pelanggan

1. Pelanggan membuka tab antarmuka pelanggan.
2. Pelanggan memilih menu dari katalog.
3. Pelanggan mengatur jumlah item di keranjang.
4. Sistem menghitung subtotal dan total.
5. Pelanggan melakukan checkout.
6. Sistem menyimpan pesanan dan mengurangi stok produk.
7. Sistem menampilkan struk dan nomor antrean.

### Alur Pegawai

1. Pegawai membuka tab sistem kasir dan admin.
2. Pegawai login menggunakan akun yang tersedia.
3. Kasir memantau pesanan masuk.
4. Kasir menandai pesanan selesai.
5. Admin dapat mengelola produk, akun, dan rekap transaksi.

## Cara Menjalankan

### Prasyarat

- JDK 11 atau versi lebih baru.
- Apache Maven atau IDE yang mendukung Maven.
- Koneksi internet pertama kali untuk mengunduh dependensi Maven jika belum tersedia.

### Menjalankan dengan Maven

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.lunacaffe.Main"
```

### Menjalankan dari IDE

1. Buka folder proyek sebagai Maven Project.
2. Tunggu IDE membaca `pom.xml`.
3. Jalankan file `src/main/java/com/lunacaffe/Main.java`.
4. Database dan data awal akan disiapkan otomatis saat aplikasi berjalan.

## Dependensi

| Dependensi | Versi | Fungsi |
| --- | --- | --- |
| FlatLaf | 3.2.1 | Tampilan modern pada Java Swing |
| SQLite JDBC | 3.42.0.0 | Koneksi Java ke SQLite |

## Dokumentasi dan Demo

- Dokumentasi akademik: buka `index.html`.
- Demo visual berbasis web: buka `demo.html`.
- File lama `demo_lunacaffe.html` tetap tersedia dan diarahkan ke `demo.html`.

## Kesimpulan

LunaCaffe POS menunjukkan penerapan PBO pada aplikasi desktop dengan studi kasus yang relevan. Sistem ini tidak hanya menampilkan konsep dasar class dan object, tetapi juga memanfaatkan inheritance, encapsulation, abstraction, polymorphism, MVC, DAO, serta database lokal. Dengan fitur katalog, checkout, antrean pesanan, manajemen produk, manajemen akun, dan rekap CSV, proyek ini dapat digunakan sebagai contoh implementasi PBO yang terstruktur dan dapat dikembangkan lebih lanjut.
