<h1 align="center">🌙 Lunacaffe POS System</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-11%2B-orange?style=flat-square&logo=java" alt="Java Version">
  <img src="https://img.shields.io/badge/GUI-Swing%20FlatLaf-blue?style=flat-square" alt="GUI Mode">
  <img src="https://img.shields.io/badge/Database-SQLite-0f80cc?style=flat-square&logo=sqlite" alt="SQLite">
  <img src="https://img.shields.io/badge/Architecture-MVC%20Pattern-brightgreen?style=flat-square" alt="MVC">
  <img src="https://img.shields.io/badge/Course-PBO%20OOP-purple?style=flat-square" alt="PBO">
</p>

## 📖 Ringkasan Proyek
**Lunacaffe Point of Sales (POS)** adalah aplikasi kasir *desktop* cerdas berbasis Java, dikembangkan untuk mendigitalisasi operasional transaksi sebuah kafe/restoran modern. Sistem ini memiliki peran ganda (*Dual Interface*): 
1. **Kiosk Area** (*Self-Service*) di layar depan bagi pelanggan untuk memesan secara mandiri.
2. **Dashboard Internal** di balik meja khusus para pegawai (Kasir & Admin).

> 💡 **Dokumentasi Lengkap:** Untuk panduan penggunaan sistem yang interaktif dan detail, silakan buka **[`index.html`](https://firgiawann.github.io/lunacaffe)** melalui *browser* Anda!

---

## 🚀 Fitur & Dokumentasi Sistem
Aplikasi ini dikembangkan dengan membagi wewenang/role berbasis **RBAC (Role-Based Access Control)** pada tiga entitas nyata:

- **Modul Pelanggan (Guest) / Tanpa Login:** Mampu mencari (*live search*) kopi dari katalog, *filter* kategori (*Hot/Cold/Snack*), menggunakan fitur keranjang otomatis (dengan pengecekan *real-time* sisa gudang), dan melakukan pembayaran lalu merilis digital struk QR antrean.
- **Modul Kasir (Front-Office) / Akun `kasir1:123`:** Memantau letak pergerakan pesanan *(Live Queue)*, menandai penyelesaian order, dan membimbing sirkulasi menu tanpa merubah infrastruktur master data.
- **Modul Super Admin / Akun `admin:123`:** Menguasai tata kelola mutlak. Meliputi C-R-U-D harga dan penambahan stok Gudang Menu, mendaftarkan dan memblokir akun Kasir, hingga me-*generate* laporan Keuangan CSV harian secara instan.

---

## 🛠️ Arsitektur & Teknologi
Proyek ini dibangun tidak dengan tumpukan kode yang kusam, melainkan dipisahkan lewat Pola Arsitektur **Model-View-Controller (MVC)**:
* **Model**: Struktur OOP (*Java Class*) pencetak objek Aktor maupun Inventaris.
* **View**: Implementasi Front-end Desktop menggunakan **Java Swing** dengan injeksi tema modern dari *library* **FlatLaf**.
* **Controller**: Logika jembatan dan lapisan DAO (*Data Access Object*) pengirim statemen relasional.
* **Database**: **SQLite** V.3 yang langsung tertanam otomatis tanpa *server hosting* (Schema: `users`, `menus`, `orders`, `order_details`).

---

## 🎓 Pemenuhan Tugas Akademik (PBO)

Bagian ini didedikasikan untuk menjawab pertanyaan pengujian Mata Kuliah Pemrograman Berorientasi Objek.

### ❓ 3 Pertanyaan Dasar
1. **Apa Aplikasi yang Dibuat?**
   Aplikasi kasir kafe (*Point of Sales*) cerdas dengan fitur pemesanan mandiri (*self-service kiosk*) bagi pelanggan, serta fitur manajemen pemrosesan order dan inventaris bagi pegawai.
2. **Siapa Saja User/Aktornya?**
   Terdapat 3 ruang lingkup aktor: **Pelanggan** (tanpa batas *login* dan melihat katalog depan), **Kasir** (memantau pergerakan order dan kasir), dan **Admin** (spesialis kontrol aset dan pendaftaran karyawan).
3. **Bagaimana Struktur & Abstrak Kelasnya?**
   - **Induk Tertinggi (`abstract`):** `Aktor` (Memaksa lahirnya *method* `masukSistem()`).
   - **Keturunan Pertama:** `Pembeli` (Bebas gerbang) & `Pegawai` (Ter-enkapsulasi sandi gembok rahasia).
   - **Keturunan Kedua:** Berlanjut dari `Pegawai` menjadi `Kasir` dan `Admin` (Dengan pewarisan *Inheritance* solid).

### ✅ Pembuktian 6 Pilar Utama (OOP Principles)
Sistem ini mematuhi standar *Object-Oriented Programming* 100%:
* **1. Class (Cetak Biru):** Pembentukan landasan tak terlihat seperti `class Menu`.
* **2. Object (Wujud Nyata):** Instansiasi di alam memori, contoh: `new Pesanan()`.
* **3. Encapsulation (Enkapsulasi Privasi):** Variabel `private stok` yang haram disentuh dari luar kecuali lewat jalan modifikasi formal `setStok()`.
* **4. Inheritance (Pewarisan Gen):** `class Kasir extends Pegawai`, secara otonom meminjam identitas atribut *username* bapaknya tanpa duplikasi tata kodingan.
* **5. Polymorphism (Banyak Wujud Berbeda):** Peniupan *Overriding* pada rutinitas `masukSistem()`. Aktor pelanggan masuk ke kasir; Aktor pegawai dicegat di kotak pengecekan sandi.
* **6. Abstraction (Kewajiban Mutlak):** `abstract class Aktor` dipatenkan agar sistem Java menolak wujud "Aktor siluman", mengharuskan ia hanya berevolusi menjadi wujud kongkrit entitas Pegawai atau Pelanggan.

---

## 💻 Panduan Implementasi & Teknis Instalasi (Local Deployment)

Proyek ini dibangun di atas infrastruktur **Maven**. Oleh karena itu, semua *dependency* (seperti FlatLaf UI & relasi SQLite-JDBC) akan diunduh secara otomatis dari *repository* pusat. Berikut adalah langkah teknis pengoperasiannya (*running*) ke dalam berbagai lingkungan IDE:

### Persyaratan Awal (Prerequisites)
- **Java JDK (11 atau 17+)** telah ter-*install* dan terdaftar di *Environment Variables* (Path) OS Windows/Linux Anda.
- **Apache Maven** (Opsional jika IDE Anda sudah membawanya secara *built-in*).

---

### Opsi A: Menjalankan di Lingkungan Apache NetBeans 
*NetBeans sangat bersahabat dengan Maven dan pengembangan antarmuka GUI Swing secara umum.*
1. Buka NetBeans, pilih menu navigasi ujung kiri **`File` > `Open Project...`**
2. Cari dan pilih folder repositori ini (Biasanya ditandai dengan ikon "ma" biru kecil yang berarti Maven Project).
3. Biarkan *loading bar* yang ada di sisi bawah menyelesaikan tugasnya. NetBeans secara otomatis akan membaca berkas `pom.xml` dan mengunduh dependensi yang diperlukan.
4. Klik kanan pada ikon Folder Proyek (`LunaCaffe`), lalu tekan instruksi **`Clean and Build`**.
5. Rentangkan pundi folder *Source Packages* dan cari *file*: `src/main/java/com/lunacaffe/Main.java`.
6. Klik kanan persis pada *file* Main tersebut, lalu lontarkan pelatuk **`Run File`** *(Shortcut: Shift + F6)*.

### Opsi B: Menjalankan di Lingkungan Visual Studio Code (VS Code)
1. Buka folder *root* proyek (`LunaCaffe`) di VS Code.
2. Pastikan VS Code Anda sudah dipasangi pelengkap Ekstensi wajib dari Microsoft:
   - **Extension Pack for Java** (`vscjava.vscode-java-pack`)
3. Begitu folder dibuka, Java Extension akan bereaksi memindai `pom.xml` secara otomatis (Perhatikan ikon memuat "Java" yang berjalan di pojok kanan bawah terminal).
4. Sorot tab *Explorer*, carilah *file* `/src/main/java/com/lunacaffe/Main.java`.
5. Tepat melayang di bagian atas deklarasi `public static void main(...)`, baris teks interaktif berwarna abu-abu/biru bertuliskan `▶ Run` atau `Debug` akan unjuk gigi. 
6. Silakan klik teks interaktif **`▶ Run`** tersebut.

### Opsi C: Eksekusi Lewat Terminal Inti (Command-Prompt / Bash)
Bila ingin merasakan pengalaman terminal secara murni, buka *Command Prompt* atau *Terminal* VS code Anda, arahkan direktorinya `cd` ke dalam *root* (tempat `pom.xml` berada) lalu letuskan komando prompt maven ini:

> **1. Menyusun Tumpukan Kode dan Unduh Dependensi POM:**
```bash
mvn clean compile
```

> **2. Menjalankan Main-Class Aplikasinya:**
```bash
mvn exec:java -Dexec.mainClass="com.lunacaffe.Main"
```

> ⚙️ **Catatan Penutup Otomasi Sistem Data!**  
> *Setelah Java berhasil dipicu memakai Opsi A, B, maupun C di atas, modul backend `DatabaseConnection.java` secara independen akan menembakkan file benih bernama **`lunacaffe.db`** ke jantung folder tempat Anda berdiri saat ini. Data dasar Menu dan Akun Kasir juga ditanam paksa ke dalamnya.*

---

## 👥 Tim Pembangun (Kelompok 3)
Dikembangkan penuh dedikasi oleh:
- **Firgiawan Listianto** 
- **Muh. Nabil Makarimsyah** 
- **Jumaria** 
- **Kelvin Surya Putra** 

> *"Kualitas dari software tidak dinilai dari kerumitannya dibaca orang, melainkan bagaimana masalah besar mampu diurai ke rancang susunan objek-objek kecil."*
