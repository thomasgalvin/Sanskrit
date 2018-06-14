package sanskrit

enum class ContributorRole( val title: String, val abb: String ){
    Author( "Author", "aut" ),
    Adapter( "Adapter", "adp" ),
    AuthorInQuotations( "Author in Quotations", "aqt" ),
    AuthorOfAfterward( "Author of Afterward", "aft" ),
    AuthorOfIntroduction( "Author of Introduction", "aui" ),
    CopyrightHolder( "Copyright Holder", "cph" ),
    CoverDesigner( "Cover Designer", "cov" ),
    Designer( "Designer", "dsr" ),
    Distributor( "Distributor", "dst" ),
    Editor( "Editor", "edt" ),
    Interviewee( "Interviewee", "ive" ),
    Interviewer( "Interviewer", "ivr" ),
    Lyricist( "Lyricist", "lyr" ),
    Other( "Other", "otr" ),
    Photographer( "Photographer", "pht" ),
    Proofreader( "Proofreader", "pfr" ),
    Publisher( "Publisher", "pbl" ),
    Reporter( "Reporter", "rpt" ),
    ResearchTeamHead( "Research Team Head", "rth" ),
    ResearchTeamMember( "Research Team Member", "rtm" ),
    Reviewer( "Reviewer", "rev" ),
    ScientificAdvisor( "Scientific Advisor", "sad" ),
    StandardsBody( "Standards Body", "stn" ),
    ThesisAdvisor( "Thesis Advisor", "ths" ),
    Transcriber( "Transcriber", "trc" ),
    Translator( "Translator", "trl" ),
    WriterOfAccompanyingMaterial( "Writer of Accompanying Material", "wam" ),
}

data class Contributor( val name: String, val sortByName: String, val role: ContributorRole )