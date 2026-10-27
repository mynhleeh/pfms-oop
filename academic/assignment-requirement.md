# Assignment Requirements

## Basic-level topics

| # | Topic | Key subjects | Key OOP features |
| :--- | :--- | :--- | :--- |
| 1 | **Library Management System** | Book, Member, Librarian, Loan | Association, inheritance |
| 2 | **Student Management System** | Student, Course, Department, Grade | Composition, collections |
| 3 | **Bank Account Management** | Customer, Account, Transaction | Inheritance, polymorphism |
| 4 | **Hotel Room Management** | Room, Guest, Reservation | Association, enum |
| 5 | **Cinema Ticket Booking** | Movie, Theater, Show, Seat, Ticket | Composition |
| 6 | **Vehicle Rental System** | Vehicle, Car, Motorbike, Customer, Rental | Inheritance |
| 7 | **Gym Membership System** | Member, Trainer, Membership, Course | Association |
| 8 | **Restaurant Ordering System** | MenuItem, Table, Order, OrderItem | Composition |
| 9 | **Parking Lot Management** | Vehicle, ParkingSpot, Ticket | Polymorphism |
| 10 | **Personal Expense Manager** | User, Account, Transaction, Category | Collections, file I/O |

## Intermediate-level topics

| # | Topic | Key subjects | Applicable patterns/OOP concepts |
| :--- | :--- | :--- | :--- |
| 11 | **University Course Registration** | đăng ký môn, prerequisite, lịch học | Strategy, Observer |
| 12 | **Hospital/Clinic Management** | bệnh nhân, bác sĩ, lịch khám, hóa đơn | Factory, Strategy |
| 13 | **Online Shopping System** | product, cart, order, payment | Strategy, Factory |
| 14 | **Food Delivery System** | restaurant, order, driver, delivery | State, Strategy |
| 15 | **Learning Management System** | course, lesson, assignment, grading | Factory, Observer |
| 16 | **Online Examination System** | question, exam, submission, grading | Factory, Strategy |
| 17 | **Event & Ticketing System** | event, ticket, booking, check-in | Factory, Observer |
| 18 | **Car Service Center** | vehicle, repair, technician, parts | Strategy, Factory |
| 19 | **Dormitory Management System** | student, room, contract, utility bill | State, Strategy |
| 20 | **Task & Project Management System** | project, task, member, comment | Observer, State |

## Advanced-level topics

| # | Đề tài | Nội dung | OOP/Pattern nổi bật |
| :--- | :--- | :--- | :--- |
| 21 | **Airline Reservation System** | flight, aircraft, seat, passenger, booking | State, Strategy, Factory |
| 22 | **Logistics & Parcel Tracking System** | package, warehouse, shipment, tracking | State, Observer |
| 23 | **Ride-Hailing System** | passenger, driver, vehicle, trip, payment | Strategy, Observer |
| 24 | **Smart Home Management System** | sensors, devices, rules, automation | Observer, Command |
| 25 | **Smart Traffic Management System** | intersection, road, vehicle, traffic light | State, Observer |
| 26 | **Digital Wallet & Payment System** | wallet, transaction, payment, transfer | Strategy, Factory |
| 27 | **Insurance Management System** | policy, customer, claim, compensation | Strategy, State |
| 28 | **Warehouse & Inventory Management** | product, supplier, warehouse, order | Observer, Factory |
| 29 | **Game Tournament Management** | team, player, match, tournament, ranking | Strategy, State |
| 30 | **Food Delivery Marketplace** | restaurant, customer, driver, order, promotion | Strategy, State, Observer |

## The report and presentation slides should demonstrate...

| Criteria | Points |
| :--- | ---: |
| Problem analysis & requirements | 10 |
| UML & object modeling | 10 |
| Encapsulation & class design | 10 |
| Association/composition | 10 |
| Inheritance & polymorphism | 10 |
| Interfaces & abstraction | 10 |
| Collections, generics, exceptions | 10 |
| SOLID/design patterns | 10 |
| Code quality & testing | 10 |
| Demo & presentation | 10 |
| **Total** | **100** |

## SOLID

| Chữ | Nguyên lý | Ý nghĩa ngắn gọn |
| :--- | :--- | :--- |
| **S** | Single Responsibility Principle | Một class nên có **một trách nhiệm chính** |
| **O** | Open/Closed Principle | **Mở để mở rộng**, đóng để sửa đổi |
| **L** | Liskov Substitution Principle | Lớp con phải có thể **thay thế lớp cha** |
| **I** | Interface Segregation Principle | Không ép class phụ thuộc vào interface nó **không cần** |
| **D** | Dependency Inversion Principle | Phụ thuộc vào **abstraction**, không phụ thuộc implementation cụ thể |

## Mile stones

- **Milestone 1 – Problem Analysis** → mô tả requirements và use cases (Sau chương 1).
- **Milestone 2 – Object-Oriented Analysis** → xác định objects/classes và trách nhiệm (Sau chương 3).
- **Milestone 3 – UML Design** → class diagram, relationships, inheritance (Sau chương 6).
- **Milestone 4 – Core Implementation** → Java classes + collections + exception handling (Sau chương 8).
- **Milestone 5 – Design Improvement** → SOLID + refactoring + design patterns (Sinh viên tự làm, giảng viên không giám sát).
- **Milestone 6 – Final Product** → testing + demo + presentation (Buổi thuyết trình).

## Choosing Your Project: What to Discuss With Your Team

**Problem fit:** does the idea naturally use the topics from this chapter — variables, control structures, and console I/O — without requiring knowledge you haven't learned yet?

**Scope:** can a small team realistically design, code, and test it within the semester timeline? Prefer a small idea done well over an ambitious idea left unfinished.

**Data to manage:** what will your program store and manipulate (student records, book catalog, quiz questions, expenses)?

**User interaction:** will the user interact through a simple text menu on the console for now, evolving toward a GUI later in the course?

**Team roles:** who owns which class or feature, and how will you integrate everyone's work before the deadline?

> **Deliverable:** proposal naming your application, its core menu options, analysis, design — due **one week after reminded**.
