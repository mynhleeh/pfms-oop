# Systems and software engineering — Life cycle processes — Requirements engineering

Ingénierie des systèmes et du logiciel — Processus du cycle de vie — Ingénierie des exigences

SIST Preview

(https://ecommerce.sist.si)

# SIST Preview (https://ecommerce.sist.si)

ISO/IEC/IEEE 29148:2018

https://ecommerce.sist.si/catalog/standards/iso/8cf2bc2b-8b5e-4907-a82a-d1c5676c9e85/iso-iec-ieee-29148-2018

<div style="text-align: center;"><img src="imgs/img_in_image_box_71_1183_153_1252.jpg" alt="Image" width="6%" /></div>


## COPYRIGHT PROTECTED DOCUMENT

## © ISO/IEC 2018

© IEEE 2018

All rights reserved. Unless otherwise specified, or required in the context of its implementation, no part of this publication may be reproduced or utilized otherwise in any form or by any means, electronic or mechanical, including photocopying, or posting on the internet or an intranet, without prior written permission. Permission can be requested from either ISO or IEEE at the respective address below or ISO's member body in the country of the requester.

ISO copyright office

CP 401 • Ch. de Blandonnet 8

CH-1214 Vernier, Geneva

Phone: +41 22 749 01 11

Fax: +41 22 749 09 47

Email: copyright@iso.org

Website: www.iso.org

Institute of Electrical and Electronics Engineers, Inc

3 Park Avenue, New York

NY 10016-5997, USA

Published in Switzerland

Email: stds.ipr@ieee.org

Website: www.ieee.org

© ISO/IEC 2018 – All rights reserved

© IEEE 2018 – All rights reserved

## Contents

Foreword vi  
Introduction vii  
1 Scope 1  
2 Normative references 1  
3 Terms, definitions and abbreviated terms 1  
3.1 Terms and definitions 1  
3.2 Abbreviated terms 7  
4 Conformance 8  
4.1 Intended usage 8  
4.2 Full conformance 8  
4.3 Conformance to processes 8  
4.4 Conformance to information item content 8  
4.5 Tailored conformance 9  
4.5.1 Processes 9  
4.5.2 Information items 9  
5 Concepts 9  
5.1 General 9  
5.2 Requirements fundamentals 9  
5.2.1 General 9  
5.2.2 Stakeholders 9  
5.2.3 Transformation of needs into requirements 10  
5.2.4 Requirements construct 10  
5.2.5 Characteristics of individual requirements 12  
5.2.6 Characteristics of a set of requirements 13  
5.2.7 Requirement language criteria 14  
5.2.8 Requirements attributes 14  
5.3 Practical considerations 16  
5.3.1 Application of iteration and recursion 16  
5.3.2 Iteration and recursion in requirements engineering 17  
5.4 Requirement information items 18  
6 Processes 20  
6.1 Requirement processes 20  
6.1.1 Guidelines for processes 20  
6.2 Business or mission analysis process 21  
6.2.1 Purpose 21  
6.2.2 Outcomes 22  
6.2.3 Activities and tasks 22  
6.3 Stakeholder needs and requirements definition process 26  
6.3.1 Purpose 26  
6.3.2 Outcomes 26  
6.3.3 Activities and tasks 26  
6.4 System [System/Software] Requirements definition process 34  
6.4.1 Purpose 34  
6.4.2 Outcomes 35  
6.4.3 Activities and tasks 35  
6.5 Requirements engineering activities in other technical processes 41  
6.5.1 Requirements activities in architecture definition 41  
6.5.2 Requirements activities in verification 42  
6.5.3 Requirements activities in validation 44  
6.6 Requirements management 45  
6.6.1 Management overview 45  
6.6.2 Change management 46

6.6.3 Measurement for requirements 48  
  
7 Information items 50  
  
8 Guidelines for information items 51  
8.1 Requirements information item outlines 51  
8.2 Business requirements specification 51  
8.2.1 General 51  
8.2.2 BRS example outline 51  
8.3 Stakeholder requirements specification 52  
8.3.1 General 52  
8.3.2 StRS example outline 52  
8.4 System requirements specification 53  
8.4.1 General 54  
8.4.2 SyRS example outline 54  
8.5 Software requirements specification 55  
8.5.1 General 55  
8.5.2 SRS example outline 56  
  
9 Information item content 57  
9.1 General 57  
9.2 General content 57  
9.2.1 Identification 57  
9.2.2 Front matter 57  
9.2.3 Definitions 58  
9.2.4 References 58  
9.2.5 Acronyms and abbreviations 58  
9.3 Business requirements specification (BRS) content 58  
9.3.1 BRS overview 58  
9.3.2 Business purpose / ecommerce system 58  
9.3.3 Business scope 58  
9.3.4 Business overview 59  
9.3.5 Major Stakeholders 59  
9.3.6 Business environment 59  
9.3.7 Mission, goals and objectives 59  
9.3.8 Business model 59  
9.3.9 Information environment 59  
9.3.10 Business processes 59  
9.3.11 Business operational policies and rules 60  
9.3.12 Business operational constraints 60  
9.3.13 Business operational modes 60  
9.3.14 Business operational quality 60  
9.3.15 Business structure 60  
9.3.16 High-level operational concept 60  
9.3.17 High-level operational scenarios 60  
9.3.18 Other high-level life-cycle concepts 61  
9.3.19 Project constraints 61  
9.4 Stakeholder requirements specification (StRS) content 61  
9.4.1 StRS overview 61  
9.4.2 Stakeholder purpose 61  
9.4.3 Stakeholder scope 61  
9.4.4 Overview 61  
9.4.5 Stakeholders 61  
9.4.6 Business environment 61  
9.4.7 Mission, goals and objectives 62  
9.4.8 Business model 62  
9.4.9 Information environment 62  
9.4.10 System processes 62  
9.4.11 System operational policies and rules 62  
9.4.12 Operational constraints 62

System operation ..... 75

## Foreword

ISO (the International Organization for Standardization) and IEC (the International Electrotechnical Commission) form the specialized system for worldwide standardization. National bodies that are members of ISO or IEC participate in the development of International Standards through technical committees established by the respective organization to deal with particular fields of technical activity. ISO and IEC technical committees collaborate in fields of mutual interest. Other international organizations, governmental and nongovernmental, in liaison with ISO and IEC, also take part in the work. In the field of information technology, ISO and IEC have established a joint technical committee, ISO/IEC JTC 1.

The procedures used to develop this document and those intended for its further maintenance are described in the ISO/IEC Directives, Part 1. In particular, the different approval criteria needed for the different types of ISO documents should be noted. This document was drafted in accordance with the rules given in the ISO/IEC Directives, Part 2 (see www.iso.org/directives).

IEEE Standards documents are developed within the IEEE Societies and the Standards Coordinating Committees of the IEEE Standards Association (IEEE-SA) Standards Board. The IEEE develops its standards through a consensus development process, approved by the American National Standards Institute, which brings together volunteers representing varied viewpoints and interests to achieve the final product. Volunteers are not necessarily members of the Institute and serve without compensation. While the IEEE administers the process and establishes rules to promote fairness in the consensus development process, the IEEE does not independently evaluate, test, or verify the accuracy of any of the information contained in its standards.

Attention is drawn to the possibility that some of the elements of this document may be the subject of patent rights. ISO and IEC shall not be held responsible for identifying any or all such patent rights. Details of any patent rights identified during the development of the document will be in the Introduction and/or on the ISO list of patent declarations received (see  $ \underline{\text{www.iso.org/patents}} $).

Any trade name used in this document is information given for the convenience of users and does not constitute an endorsement.

For an explanation of the voluntary nature of standards, the meaning of ISO specific terms and expressions related to conformity assessment, as well as information about ISO's adherence to the World Trade Organization (WTO) principles in the Technical Barriers to Trade (TBT), see  $ \underline{\text{www.iso.org/iso/foreword.html}} $.

This document was prepared by Joint Technical Committee ISO/IEC JTC 1, Information technology, Subcommittee SC 7, Systems and software engineering, in cooperation with the Systems and Software Engineering Standards Committee of the IEEE Computer Society, under the Partner Standards Development Organization cooperation agreement between ISO and IEEE.

This second edition cancels and replaces the first edition (ISO/IEC/IEEE 29148:2011), which has been technically revised.

Changes in this revision of ISO/IEC/IEEE 29148 were developed in response to the revision of ISO/IEC/IEEE 15288 and ISO/IEC/IEEE 12207. The purpose of these revisions is to accomplish the harmonization of the structures and contents of the two documents, while supporting the requirements of the assessment community.

Any feedback or questions on this document should be directed to the user's national standards body. A complete listing of these bodies can be found at  $ \underline{\text{www.iso.org/members.html}} $.

## Introduction

This document provides a unified treatment of the processes and products involved in engineering requirements throughout the life cycle of systems and software. It provides details for the construct of well-formed textual requirements, to include characteristics and attributes, in the context of system and software engineering. This document also provides guidance for the implementation of requirements related processes from ISO/IEC/IEEE 15288 and ISO/IEC/IEEE 12207. Finally, this document identifies information items related to requirements engineering and their content.

SIST Preview

(https://ecommerce.sist.si)

SIST Preview

(https://ecommerce.sist.si)

# Systems and software engineering — Life cycle processes — Requirements engineering

## 1 Scope

## This document:

— specifies the required processes implemented in the engineering activities that result in requirements for systems and software products (including services) throughout the life cycle;

— provides guidelines for applying the requirements and requirements-related processes described in ISO/IEC/IEEE 15288 and ISO/IEC/IEEE 12207;

— specifies the required information items produced through the implementation of the requirements processes;

— specifies the required contents of the required information items;

— provides guidelines for the format of the required and related information items.

This document is applicable to:

— those who use or plan to use ISO/IEC/IEEE 15288 and ISO/IEC/IEEE 12207 on projects dealing with man-made systems, software-intensive systems, software and hardware products, and services related to those systems and products, regardless of the project scope, product(s), methodology, size or complexity;

— anyone performing requirements engineering activities to aid in ensuring that their application of the requirements engineering processes conforms to ISO/IEC/IEEE 15288 and/or ISO/IEC/IEEE12207;

— those who use or plan to use ISO/IEC/IEEE 15289 on projects dealing with man-made systems, software-intensive systems, software and hardware products and services related to those systems and products, regardless of the project scope, product(s), methodology, size or complexity;

— anyone performing requirements engineering activities to aid in ensuring that the information items developed during the application of requirements engineering processes conforms to ISO/IEC/IEEE 15289.

## 2 Normative references

The following documents are referred to in the text in such a way that some or all of their content constitutes requirements of this document. For dated references, only the edition cited applies. For undated references, the latest edition of the referenced document, (including any amendments) applies.

ISO/IEC/IEEE 15288:2015, Systems and software engineering — System life cycle processes

ISO/IEC/IEEE 12207:2017, Systems and software engineering — Software life cycle processes

## 3 Terms, definitions and abbreviated terms

### 3.1 Terms and definitions

For the purposes of this document, the terms and definitions given in ISO/IEC/IEEE 15288, ISO/IEC/IEEE 12207 and the following apply.

© ISO/IEC 2018 – All rights reserved

© IEEE 2018 – All rights reserved

ISO, IEC and IEEE maintain terminological databases for use in standardization at the following addresses:

— ISO Online browsing platform: available at http://www.iso.org/obp

— IEC Electropedia: available at http://www.electropedia.org/

— IEEE Standards Dictionary Online: available at http://dictionary.ieee.org

NOTE Definitions for other system and software engineering terms typically can be found in ISO/IEC/IEEE 24765, available at  $ \underline{\text{www.computer.org/sevocab}} $.

#### 3.1.1 

## acquirer

stakeholder (3.1.28) that acquires or procures a product or service from a supplier (3.1.31)

Note 1 to entry: Other terms commonly used for an acquirer are buyer, customer (3.1.9), owner, purchaser or internal/organizational sponsor.

[SOURCE: ISO/IEC/IEEE 15288:2015, 4.1.1]

#### 3.1.2 

## attribute

inherent property or characteristic of an entity that can be distinguished quantitatively or qualitatively by human or automated means

Note 1 to entry: ISO 9000 distinguishes two types of attributes: a permanent characteristic existing inherently in something; and an assigned characteristic of a product, process, or system (e.g., the price of a product, the owner of a product).

[SOURCE: ISO/IEC 25000:2014, 4.1, modified — The original NOTE 1 has been removed; NOTE 2 has become Note 1 to entry.]

#### 3.1.3 

## baseline

formally approved version of a configuration item, regardless of media, formally designated and fixed at a specific time during the configuration item's life cycle

[SOURCE: ISO/IEC/IEEE 15288:2015, 4.1.10]

#### 3.1.4 

## business requirements specification

structured collection of the requirements (3.1.19) (business or mission problem or opportunity definition, concepts, and required conditions of solutions) of the business or mission and its relation to the external environment

#### 3.1.5 

## concept of operations

verbal and graphic statement, in broad outline, of an organization's assumptions or intent in regard to an operation or series of operations

Note 1 to entry: The concept of operations frequently is embodied in long-range strategic plans and annual operational plans. In the latter case, the concept of operations in the plan covers a series of connected operations to be carried out simultaneously or in succession. The concept is designed to give an overall picture of the organization operations. See also operational concept (3.1.16).

Note 2 to entry: The concept of operations provides the basis for bounding the operating space, system capabilities, interfaces and operating environment.

Note 3 to entry: The concept of operations includes how an enterprise or organization intends to employ available human and technological resources to achieve one or more outcomes.

[SOURCE: ANSI/AIAA G-043A-2012e]

#### 3.1.6 

## condition

measurable qualitative or quantitative attribute (3.1.2) that is stipulated for a requirement (3.1.19) and that indicates a circumstance or event under which a requirement applies

#### 3.1.7 

## constraint

externally imposed limitation on the system, its design, or implementation or on the process used to develop or modify a system

Note 1 to entry: A constraint is a factor that is imposed on the solution by force or compulsion and may limit or modify the design.

#### 3.1.8 

## context of use

users (3.1.35), tasks, equipment (hardware, software and materials), and the physical and social environments in which a product is used

[SOURCE: ISO/IEC 25000:2014, 4.2]

#### 3.1.9 

## customer

person or organization that could or does receive a product or a service that is intended for or required by this person or organization

Note 1 to entry: Customers are a subset of stakeholders (3.1.28).

Note 2 to entry: A customer can be internal or external to the organization.

[SOURCE: ISO 9000:2015, 3.2.4, modified — The original Note 1 to entry has become Note 2 to entry; a new Note 1 to entry has been introduced and the EXAMPLE has been removed.]

#### 3.1.10 

## derived requirement

requirement (3.1.19) deduced or inferred from the collection and organization of requirements into a particular system configuration and solution

Note 1 to entry: The next higher level requirement is referred to as a “parent” requirement while the derived requirement from this parent is called a “child” requirement.

Note 2 to entry: A derived requirement is typically identified during the elicitation of stakeholder (3.1.28) requirements, requirements analysis, trade studies or validation (3.1.36).

#### 3.1.11 

## developer

individual or organization that performs development activities (including requirements analysis, design, testing through acceptance) during the system or software life-cycle process

Note 1 to entry: Developers are a subset of stakeholders (3.1.28).

#### 3.1.12 

[SOURCE: ISO/IEC 25000:2014, 4.6, modified — Note 1 to entry has been added.]

uniquely identified unit of information for human use

## document

EXAMPLE report, specification, manual or book in printed or electronic form.

Note 1 to entry: A document can be a single information item (3.1.14), or part of a larger information item (3.1.14).

Note 2 to entry: Documents include both paper and electronic documents.

Note 3 to entry: The use of the term document is not intended to preclude interpretation in the broader sense of “documentation”, especially as it applies to model-based engineering or conformance.

[SOURCE: ISO/IEC/IEEE 15289:2017, 3.1.10, modified – Notes 2 and 3 to entry have been added.]

#### 3.1.13 

## human systems integration

interdisciplinary technical and management processes for integrating human considerations within and across all system elements

## [SOURCE: INCOSE SEHbk 4:2015]

#### 3.1.14 

## information item

separately identifiable body of information that is produced, stored, and delivered for human use

[SOURCE: ISO/IEC/IEEE 15289:2017, 3.1.13, modified – Notes 1 and 2 to entry have been removed.]

#### 3.1.15 

## level of abstraction

view at a specific level of detail in a description of a system

#### 3.1.16 

## operational concept

verbal and graphic statement of an organization's assumptions or intent in regard to an operation or series of operations of a specific system or a related set of specific new, existing or modified systems

Note 1 to entry: The operational concept is designed to give an overall picture of the operations using one or more specific systems, or set of related systems, in the organization's operational environment from the users' (3.1.35) and operators' (3.1.18) perspective. It is what the enterprise or organization intends to achieve. See also concept of operations (3.1.5).

[SOURCE: ANSI/AIAA G-043A-2012e]

#### 3.1.17 

## ISO/IEC/IEEE 29148:2018

---

Date: 2023-07-01

Date: 2023-07-01

Date: 2023-07-01

## operational scenario

description of an imagined sequence of events or activities that includes the interaction of the product or service with its environment and users (3.1.35), as well as interaction among its product or service components when there is end-use significance

Note 1 to entry: Operational scenarios are used to evaluate the requirements (3.1.19) and design of the system and to verify and validate the system.

#### 3.1.18 

## operator

individual or organization that performs the operations of a system

Note 1 to entry: The role of operator and the role of user (3.1.35) can be vested, simultaneously or sequentially, in the same individual or organization.

Note 2 to entry: An individual operator combined with knowledge, skills and procedures can be considered as an element of the system.

Note 3 to entry: An operator may perform operations on a system that is operated, or of a system that is operated, depending on whether or not operating instructions are placed within the system boundary.

[SOURCE: ISO/IEC/IEEE 15288:2015, 4.1.26]

#### 3.1.19 

## requirement

statement which translates or expresses a need and its associated constraints (3.1.7) and conditions (3.1.6)

Note 1 to entry: Requirements exist at different levels in the system structure.

© ISO/IEC 2018 – All rights reserved

© IEEE 2018 – All rights reserved

Note 2 to entry: A requirement is an expression of one or more particular needs in a very specific, precise and unambiguous manner.

Note 3 to entry: A requirement always relates to a system, software or service, or other item of interest.

#### 3.1.20 

## requirements elicitation

use of systematic techniques, such as prototyping and structured surveys, to proactively identify and document customer (3.1.9) and end user (3.1.35) needs

#### 3.1.21 

## requirements engineering

interdisciplinary function that mediates between the domains of the acquirer (3.1.1) and supplier (3.1.31) to establish and maintain the requirements (3.1.19) to be met by the system, software or service of interest

Note 1 to entry: Requirements engineering is concerned with discovering, eliciting, developing, analyzing, verifying, validating, communicating, documenting and managing requirements.

#### 3.1.22 

## requirements management

activities that identify, document, maintain, communicate, trace and track requirements (3.1.19) throughout the life cycle of a system, product or service

#### 3.1.23 

## requirements traceability

identification and documentation of the derivation path (upward) and allocation/flow-down path (downward) of requirements (3.1.19) in the requirements set

Note 1 to entry: One or more requirements upward from which a requirement is derived are called parent requirements. A requirement downward that is derived from one or more parent requirements is called a child requirement.

#### 3.1.24 

## ISO/IEC/IEEE 29148:2018

## requirements traceability matrix

structured information artifact that links requirements (3.1.19) to their higher level requirements or needs or to lower level implementation

#### 3.1.25 

## requirements validation

confirmation that requirements (3.1.19) (individually and as a set) define the right system as intended by the stakeholders (3.1.28)

## [SOURCE: EIA 632:1999, modified]

#### 3.1.26 

## requirements verification

confirmation by examination that requirements (3.1.19) (individually and as a set) are well-formed

Note 1 to entry: This means that a requirement or a set of requirements has been reviewed to help ensure the characteristics of good requirements are achieved and the requirements set is well organized.

[SOURCE: EIA 632:1999, modified]

#### 3.1.27 

## software requirements specification

structured collection of the essential requirements (3.1.19) [functions, performance, design constraints (3.1.7) and attributes (3.1.2)] of the software and its external interfaces

[SOURCE: IEEE Std 1012:2016, modified — 'documentation' has become 'structured collection']

#### 3.1.28 

## stakeholder

individual or organization having a right, share, claim or interest in a system or in its possession of characteristics that meet their needs and expectations

Note 1 to entry: Stakeholders include, but are not limited to, end users (3.1.35), end user organizations, supporters, developers (3.1.11), producers, trainers, maintainers, disposers, acquirers (3.1.1), customers (3.1.9), operators (3.1.18), supplier organizations, accreditors and regulatory bodies.

[SOURCE: ISO/IEC/IEEE 15288:2015, 4.1.44, modified — EXAMPLE and Note 1 to entry have been removed; new Note 1 to entry has been added.]

#### 3.1.29 

## stakeholder requirements specification

structured collection of the requirements (3.1.19) [characteristics, context, concepts, constraints (3.1.7) and priorities] of the stakeholder (3.1.28) and the relationship to the external environment

#### 3.1.30 

## state

condition (3.1.6) that characterizes the behaviour of a function, subfunction or element at a point in time

Note 1 to entry: A state may be used to define sets of functions or system characteristics that can vary with time.

[SOURCE: ISO/IEC/IEEE 24765:2017, 3.3958, modified — Definition 4 has been kept; all other definitions have been removed: new Note 1 to entry has replaced the original Note 1 to entry.]

#### 3.1.31 

## supplier

organization or individual that enters into an agreement with the acquirer (3.1.1) for the supply of a product or service

Note 1 to entry: Suppliers are a subset of stakeholders (3.1.28).

Note 2 to entry: Other terms commonly used for supplier are contractor, producer, seller or vendor.

Note 3 to entry: The acquirer  $ (3.1.1) $ and the supplier sometimes are part of the same organization.

[SOURCE: ISO/IEC/IEEE 15288:2015, 4.1.45, modified — New Note 1 to entry has been added; other Notes to entry renumbered.]

#### 3.1.32 

## system-of-interest

system whose life cycle is under consideration in the context of this document

[SOURCE: ISO/IEC/IEEE 15288:2015, 4.1.48 modified — ‘document’ has replaced ‘International Standard’.]

#### 3.1.33 

## system requirements specification

structured collection of the requirements (3.1.19) [functions, performance, design constraints (3.1.7), and other attributes (3.1.2)] for the system and its operational environments and external interfaces

#### 3.1.34 

## trade-off

decision-making actions that select from various requirements (3.1.19) and alternative solutions on the basis of net benefit to the stakeholders (3.1.28)