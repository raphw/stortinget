package no.blogspot.mydailyjava

import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.annotation.*
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement

const val STORTINGET_URI = "http://data.stortinget.no"
const val EXPORT_URI = "https://data.stortinget.no/eksport/"

@XmlRootElement(namespace = STORTINGET_URI, name = "komite")
@XmlAccessorType(XmlAccessType.FIELD)
data class Committee(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "parti")
@XmlAccessorType(XmlAccessType.FIELD)
data class Party(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "fylke")
@XmlAccessorType(XmlAccessType.FIELD)
data class Area(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "stortingsperiode")
@XmlAccessorType(XmlAccessType.FIELD)
data class Period(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "fra") var from: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "til") var to: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "emne")
@XmlAccessorType(XmlAccessType.FIELD)
data class Topic(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "er_hovedemne") var main: Boolean? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "hovedemne_id") var mainId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "underemne_liste") @XmlElement(namespace = STORTINGET_URI, name = "emne") var sub: List<Topic>? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "representant")
@XmlAccessorType(XmlAccessType.FIELD)
data class Representative(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "foedselsdato") var birth: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "doedsdato") var death: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "fornavn") var firstName: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "etternavn") var lastName: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "kjoenn") var gender: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "fylke") var area: Area? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "parti") var party: Party? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "sesjon")
@XmlAccessorType(XmlAccessType.FIELD)
data class Session(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "fra") var from: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "til") var to: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "sporsmal")
@XmlAccessorType(XmlAccessType.FIELD)
data class Question(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "besvart_av") var answeredBy: Representative? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "besvart_av_minister_id") var answeredByMinisterId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "besvart_av_minister_tittel") var answeredByMinisterTitle: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "besvart_dato") var answeredDate: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av") var answeredFor: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av_minister_id") var answeredForMinisterId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av_minister_tittel") var answeredForMinisterTitle: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "datert_dato") var dated: String? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "emne_liste") @XmlElement(namespace = STORTINGET_URI, name = "emne") var sub: List<Topic>? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "flyttet_til") var movedTo: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "fremsatt_av_annen") var delayedBy: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "rette_vedkommende") var amendedConcerned: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "rette_vedkommende_minister_id") var amendedConcernedMinisterId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "rette_vedkommende_minister_tittel") var amendedConcernedMinisterTitle: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sendt_dato") var sentDate: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sporsmal_fra") var questionBy: Representative? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sporsmal_nummer") var questionNumber: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sporsmal_til") var questionTo: Representative? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sporsmal_til_minister_id") var questionToMinisterId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sporsmal_til_minister_tittel") var questionToMinisterTitle: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "status") var status: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "type") var type: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "sak")
@XmlAccessorType(XmlAccessType.FIELD)
data class ItemSummary(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "dokumentgruppe") var group: String? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "emne_liste") @XmlElement(namespace = STORTINGET_URI, name = "emne") var sub: List<Topic>? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "forslagstiller_liste") @XmlElement(namespace = STORTINGET_URI, name = "representant") var suggestedBy: List<Representative>? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "henvisning") var reference: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "innstilling_id") var suggestionId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "innstilling_kode") var suggestionCode: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "komite") var committee: Committee? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "korttittel") var shortTitle: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sak_fremmet_id") var supportId: String? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "saksordfoerer_liste") @XmlElement(namespace = STORTINGET_URI, name = "representant") var spokesmen: List<Representative>? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sist_oppdatert_dato") var lastUpdate: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "status") var state: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "type") var type: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "detaljert_sak")
@XmlAccessorType(XmlAccessType.FIELD)
data class Item(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "dokumentgruppe") var group: String? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "emne_liste") @XmlElement(namespace = STORTINGET_URI, name = "emne") var sub: List<Topic>? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "ferdigbehandlet") var done: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "henvisning") var reference: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "innstillingstekst") var suggestion: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "komite") var committee: Committee? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "korttittel") var shortTitle: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "kortvedtak") var shortText: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "parentestekst") var additionalText: String? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "publikasjon_referanse_liste") @XmlElement(namespace = STORTINGET_URI, name = "publikasjon_referanse") var publications: List<Publication>? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sak_nummer") var itemId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sak_opphav") var itemOrigin: ItemOrigin? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "saksgang") var itemProcedure: ItemProcedure? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "saksordfoerer_liste") @XmlElement(namespace = STORTINGET_URI, name = "representant") var spokesmen: List<Representative>? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "status") var state: String? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "stikkord_liste") var tags: List<String>? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "type") var type: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "vedtakstekst") var note: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "publikasjon_referanse")
@XmlAccessorType(XmlAccessType.FIELD)
data class Publication(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "eksport_id") var exportId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "lenke_tekst") var linkText: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "lenke_url") var linkUrl: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "type") var type: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "undertype ") var subType: String? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class ItemOrigin(
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "forslagstiller_liste") @XmlElement(namespace = STORTINGET_URI, name = "representant") var spokesmen: List<Representative>? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "saksgang")
@XmlAccessorType(XmlAccessType.FIELD)
data class ItemProcedure(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "saksgang_steg_liste") @XmlElement(namespace = STORTINGET_URI, name = "saksgang_steg") var step: List<ItemProcedureStep>? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "saksgang_steg")
@XmlAccessorType(XmlAccessType.FIELD)
data class ItemProcedureStep(
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "steg_nummer") var number: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "uaktuell") var current: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "sak_votering")
@XmlAccessorType(XmlAccessType.FIELD)
data class Vote(
        @XmlElement(namespace = STORTINGET_URI, name = "alternativ_votering_id") var alternativeVoteId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "antall_for") var votesFor: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "antall_ikke_tilstedet") var absent: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "antall_mot") var votesAgainst: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "behandlingsrekkefoelge") var processOrder: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "dagsorden_sak_nummer") var itemId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "fri_votering") var freeVote: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "kommentar") var comment: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "mote_kart_nummer") var cardNumber: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "personlig_votering") var personalVote: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "president") var president: Representative? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "vedtatt") var accepted: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "votering_id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "votering_metode") var voteType: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "votering_resultat_type") var voteResult: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "votering_resultat_type_tekst") var voteResultInfo: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "votering_tema") var voteTopic: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "votering_tid") var voteTime: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "voteringsforslag")
@XmlAccessorType(XmlAccessType.FIELD)
data class VoteProposal(
        @XmlElement(namespace = STORTINGET_URI, name = "forslag_id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "forslag_betegnelse") var name: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "forslag_betegnelse_kort") var shortName: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "forslag_levert_av_representant") var byRepresentative: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "forslag_paa_vegne_av_tekst") var byText: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "forslag_sorteringsnummer") var orderNumber: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "forslag_tekst") var text: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "forslag_type") var type: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "voteringsvedtak")
@XmlAccessorType(XmlAccessType.FIELD)
data class VoteDecision(
        @XmlElement(namespace = STORTINGET_URI, name = "vedtak_kode") var code: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "vedtak_kommentar") var comment: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "vedtak_nummer") var number: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "vedtak_referanse") var reference: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "vedtak_tekst") var text: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "representant_voteringsresultat")
@XmlAccessorType(XmlAccessType.FIELD)
data class VoteResult(
        @XmlElement(namespace = STORTINGET_URI, name = "vara_for") var substituteFor: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "fast_vara_for") var steadySubstituteFor: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "votering") var vote: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "representant") var reference: Representative? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "mote")
@XmlAccessorType(XmlAccessType.FIELD)
data class Meeting(
        @XmlElement(namespace = STORTINGET_URI, name = "dagsorden_nummer") var number: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "fotnote") var footnote: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "ikke_motedag_tekst") var noMeetingText: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "kveldsmote") var eveningMeeting: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "merknad") var note: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "mote_dato_tid") var time: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "mote_rekkefolge") var order: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "mote_ting") var location: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "referat_id") var protocolId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "tillegsdagsorden") var extra: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "dagsordensak")
@XmlAccessorType(XmlAccessType.FIELD)
data class MeetingAgendum(
        @XmlElement(namespace = STORTINGET_URI, name = "dagsordensak_henvisning") var protocolId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "dagsordensak_nummer") var number: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "dagsordensak_tekst") var tekst: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "dagsordensak_type") var type: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "fotnote") var footnote: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "innstilling_id") var noIdeaId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "komite_id") var committeeId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "loseforslag") var suggestion: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sak_id") var itemId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sporretime_type") var questionType: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sporsmal_id") var questionId: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "horing")
@XmlAccessorType(XmlAccessType.FIELD)
data class Hearing(
        @XmlElement(namespace = STORTINGET_URI, name = "anmodningsfrist_dato_tid") var protocolId: String? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "horing_sak_info_liste") @XmlElement(namespace = STORTINGET_URI, name = "horing_sak_info") var info: List<HearingItemInfo>? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "horingstidspunkt_liste") @XmlElement(namespace = STORTINGET_URI, name = "horingstidspunkt") var time: List<HearingTimeInfo>? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "komite") var committee: Committee? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "publisert_dato") var publishingDate: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "status") var status: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "status_info_tekst") var statusInfoText: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "type") var type: String? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class HearingItemInfo(
        @XmlElement(namespace = STORTINGET_URI, name = "sak_henvisning") var reference: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sak_id") var id: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sak_tittel") var title: String? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class HearingTimeInfo(
        @XmlElement(namespace = STORTINGET_URI, name = "tidspunkt") var reference: String? = null
)

@XmlRootElement(namespace = STORTINGET_URI, name = "horing")
@XmlAccessorType(XmlAccessType.FIELD)
data class HearingProgram(
        @XmlElement(namespace = STORTINGET_URI, name = "dato") var date: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "fotnote") var footnote: String? = null,
        @XmlElementWrapper(namespace = STORTINGET_URI, name = "horingsprogram_element_liste") @XmlElement(namespace = STORTINGET_URI, name = "horingsprogram_element") var time: List<HearingProgramElement>? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "innledning") var introduction: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "merknad") var note: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "rom_id") var roomId: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "sted") var place: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "tekst") var tekst: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "video_overforing") var broadcasting: String? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class HearingProgramElement(
        @XmlElement(namespace = STORTINGET_URI, name = "rekkefolge_nummer") var orderNumber: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "tekst") var text: String? = null,
        @XmlElement(namespace = STORTINGET_URI, name = "tidsangivelse") var timeInfo: String? = null
)

interface Consumer<in T> {

    fun onElement(element: T)

    object Printing : Consumer<Any> {
        override fun onElement(element: Any) {
            println(element)
        }
    }
}

interface Dispatcher {

    fun <T> apply(reader: ThrottledReader<T>, vararg consumers: Consumer<T>)

    object Synchronous : Dispatcher {
        override fun <T> apply(reader: ThrottledReader<T>, vararg consumers: Consumer<T>) {
            reader.doRead(*consumers)
        }
    }

    class Asynchronous(val executor: Executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) : Dispatcher {
        override fun <T> apply(reader: ThrottledReader<T>, vararg consumers: Consumer<T>) {
            executor.execute(Job(reader, *consumers))
        }

        class Job<T>(val reader: ThrottledReader<T>, vararg val consumers: Consumer<T>) : Runnable {
            override fun run() {
                reader.doRead(*consumers)
            }
        }
    }
}

val dispatcher = Dispatcher.Asynchronous()

class ThrottledReader<T>(val endpoint: String, type: Class<out T>) {

    private val unmarshaller = JAXBContext.newInstance(type).createUnmarshaller()
    private val tag = type.getAnnotation(XmlRootElement::class.java).name

    fun read(vararg consumers: Consumer<T>) {
        dispatcher.apply(this, *consumers)
    }

    fun doRead(vararg consumers: Consumer<T>) {
        val stream = URL(EXPORT_URI + endpoint).openStream()
        try {
            val reader = XMLInputFactory.newFactory().createXMLEventReader(stream)
            try {
                while (reader.hasNext()) {
                    val event = reader.peek()
                    if (event != null && event.isStartElement && (event as StartElement).name.localPart == tag) {
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val element = unmarshaller.unmarshal(reader) as T
                            consumers.forEach { it.onElement(element) }
                        } catch(exception: JAXBException) {
                            exception.printStackTrace()
                        }
                    } else {
                        reader.next()
                    }
                }
            } finally {
                reader.close()
            }
        } finally {
            stream.close()
        }
    }
}

fun main(args: Array<String>) {
    ThrottledReader("stortingsperioder", Period::class.java).read(Consumer.Printing, object : Consumer<Period> {
        override fun onElement(element: Period) {
            ThrottledReader("representanter?stortingsperiodeid=${element.id}", Representative::class.java).read(Consumer.Printing)
        }
    })
    ThrottledReader("allekomiteer", Committee::class.java).read(Consumer.Printing)
    ThrottledReader("allepartier", Party::class.java).read(Consumer.Printing)
    ThrottledReader("fylker", Area::class.java).read(Consumer.Printing)
    ThrottledReader("emner", Topic::class.java).read(Consumer.Printing)
    ThrottledReader("saksganger", ItemProcedure::class.java).read(Consumer.Printing)
    ThrottledReader("sesjoner", Session::class.java).read(Consumer.Printing, object : Consumer<Session> {
        override fun onElement(element: Session) {
            ThrottledReader("komiteer?sesjonid=${element.id}", Committee::class.java).read(Consumer.Printing)
            ThrottledReader("partier?sesjonid=${element.id}", Party::class.java).read(Consumer.Printing)
            ThrottledReader("sporretimesporsmal?sesjonid=${element.id}", Question::class.java).read(Consumer.Printing)
            ThrottledReader("interpellasjoner?sesjonid=${element.id}", Question::class.java).read(Consumer.Printing)
            ThrottledReader("skriftligesporsmal?sesjonid=${element.id}", Question::class.java).read(Consumer.Printing)
            ThrottledReader("horinger?sesjonid=${element.id}", Hearing::class.java).read(Consumer.Printing, object : Consumer<Hearing> {
                override fun onElement(element: Hearing) {
                    ThrottledReader("horingsprogram?horingid=${element.id}", HearingProgram::class.java).read(Consumer.Printing)
                }
            })
            ThrottledReader("moter?sesjonid=${element.id}", Meeting::class.java).read(Consumer.Printing, object : Consumer<Meeting> {
                override fun onElement(element: Meeting) {
                    if (element.id != "-1") {
                        ThrottledReader("dagsorden?moteid=${element.id}", MeetingAgendum::class.java).read(Consumer.Printing)
                    }
                }
            })
            ThrottledReader("saker?sesjonid=${element.id}", ItemSummary::class.java).read(Consumer.Printing, object : Consumer<ItemSummary> {
                override fun onElement(element: ItemSummary) {
                    ThrottledReader("sak?sakid=${element.id}", Item::class.java).read(Consumer.Printing)
                    ThrottledReader("voteringer?sakid=${element.id}", Vote::class.java).read(Consumer.Printing, object : Consumer<Vote> {
                        override fun onElement(element: Vote) {
                            ThrottledReader("voteringsforslag?voteringid=${element.id}", VoteProposal::class.java).read(Consumer.Printing)
                            ThrottledReader("voteringsvedtak?voteringid=${element.id}", VoteDecision::class.java).read(Consumer.Printing)
                            ThrottledReader("voteringsresultat?voteringid=${element.id}", VoteResult::class.java).read(Consumer.Printing)
                        }
                    })
                }
            })
        }
    })
}
