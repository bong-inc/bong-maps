# Gruppe kontrakt

Gruppe kontrakt for gruppe 21

| medlem             | id   |
| ------------------ | ---- |
| Frederik Raisa     | FRAI |
| Emil Jäpelt        | EMJA |
| Thomas Kilbak      | THHK |
| Jonas Røssum       | JGLR |
| Kristoffer Højelse | KRBH |

1. Organisatoriske normer
    1. Form for organisation
    
        Vi arbejder i sub-grupper eller individuelt med flere forskellige sub-tasks. Dette gør det sværere at kollidere på samme kode, men giver mulighed for at arbejde sammen og hjælpe hinanden fremad.

    2. Roller

        Vi har valgt at have *KRBH* som en fast notar og *FRAI* som ordstyrer. Andre rollers opgaver bliver en håndteret kollaborativt. F.eks. så bliver der holdt styr på deadlines, i fællesskab.

    3. Modtagelse af forslag

        Alle idéer skal mødes med åbent sind, så snakker man indbyrdes om evt. komplikationer og tager en fælles beslutning, via den efterfølgende diskussion.

    4. Diskussioner

        Hvis der er flere modstridende parter, så vil alle lægge belæg for deres sag. Hvis vi ikke når til consensus, så vil der blive afholdt flertals votering.

    5. Mødested

        Som udgangspunkt mødes gruppen på ITU, helst ved en skærm og evt. whiteboard. Andre mødesteder kan forekomme efter ønske og aftale.

    6. Dokumentation

        Gruppens arbejde vil blive dokumenteret på 4 måder
        - Logbog:

            Under hvert formelt møde, vil der bliver ført logbog af gruppens valgte notar. Her vil vigtige beslutninger og diskussioner være at finde.

        - ProjectWiki

            Udvidet dokumentation til udviklerer

        - JavaDocs

            Specifik dokumentation

        - JaCoCo

            Coverage library

        - Git-Commit

            Her kan en historik af alle ændringer i kodebasen findes

        - Changelog

            Her er større ændring til produktet noteret

2. Arbejdsmæssige normer
    1. Ambitionsniveau

        Ambitionsniveauet ligger højt, med vægt på et meget tilfredsstillende produkt og en lærings fyldt arbejdsprocess. Alle parter skal have fuld forståelse for implementationen. Der skal være plads til sjov, hygge og extra features, så længe man først sørger for minimum viable product, bliver opnået, og disse features udfordrer ens egne evner.

    2. Hastighed

        Alle arbejder efter egen evne, hastighed og miljø. Så længe man respekterer at der skal være plads til alle og at man ikke forstyrrer hinandens flow.

    3. Grundighed

        Vi har tænkt os at arbejde efter et fælles betegnet minimum viable product. Koden skal dokumenteres tilstrækkeligt, git commits skal være sigende og alt kode skal følge en ensartet kode formatering (vi bruger dog ikke Prettier eller lign.).

    4. Arbejdstider

        Vi forventer at bruge ca. 20 timer om ugen på selve projektet. Vi har nogle faste arbejdstider, og herefter nogle “flex-tider”, hvor vi indbyrdes vælger at møde ud over det fastlagte skema.

    5. Fælleskasse

        Forsinket, ikke mødt op eller ikke opfyldt krav: 20 kr i fælleskassen. (Gælder kun forsinkelser som ikke er relateret til forsinkede tog, eller lignende.)
        Fælleskassen bliver brugt til fælles begivenheder, pizza, etc.
        Man skal sende pengende til mobilepay-boxen: 4084FT.

    6. Mødepligt og sygdom
        
        Alle medlemmer møder til de tider som er skemalagt i google calendar. Faste frameldinger er specificeret ved at person er markeret med *optional* tag. Ved sygdom skal man informere om det på messenger tideligst muligt. Det forventes at man godt kan arbejde hjemmefra selvom man er syg (ved mindre man er *meget* syg).

3. Tekniske normer
   1. Sprog

        Kodebasen skal skrive i Java. Kommentar, navngivning af klasser, etc. skal skrives i forståeligt Engelsk, hvor dokumentation i henhold til projektet skal skrives på Dansk.

   2. Versionsstyring

        Vi bruger Github og et centralt repository til versionssytring, for at sikre et kontinuerlig workflow.

   3. Udviklingsmiljø

        Alle kan bruge deres foretrukne udviklingsmiljø, da vi bruger Gradle som buildtool.

   4. Build tool chain

        Vi bruger Gradle som buildtool, for at skabe et agnostisk udviklingsmiljø der kan distribuere til forskellige platforme, samt udvikles på uanset IDE/Kode editor.
