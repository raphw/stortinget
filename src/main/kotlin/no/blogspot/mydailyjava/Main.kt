package no.blogspot.mydailyjava

import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.annotation.*
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement
import kotlin.reflect.KProperty
import kotlin.reflect.declaredMemberProperties
import kotlin.reflect.jvm.javaField

const val STORTINGET_URI = "http://data.stortinget.no"
const val EXPORT_URI = "https://data.stortinget.no/eksport/"

interface Node {
    var id: String?
}

interface Skip {
    fun value(): Any?
    fun property(): KProperty<*>
}

@XmlRootElement(namespace = STORTINGET_URI, name = "komite")
@XmlAccessorType(XmlAccessType.FIELD)
data class Committee(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "parti")
@XmlAccessorType(XmlAccessType.FIELD)
data class Party(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "fylke")
@XmlAccessorType(XmlAccessType.FIELD)
data class Area(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "stortingsperiode")
@XmlAccessorType(XmlAccessType.FIELD)
data class Period(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fra") var from: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "til") var to: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "emne")
@XmlAccessorType(XmlAccessType.FIELD)
data class Topic(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "er_hovedemne") var main: Boolean? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "hovedemne_id") var mainId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "underemne_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "emne") var sub: List<Topic>? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "representant")
@XmlAccessorType(XmlAccessType.FIELD)
data class Representative(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "foedselsdato") var birth: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "doedsdato") var death: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fornavn") var firstName: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "etternavn") var lastName: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "kjoenn") var gender: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fylke") var area: Area? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "parti") var party: Party? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "sesjon")
@XmlAccessorType(XmlAccessType.FIELD)
data class Session(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fra") var from: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "til") var to: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "sporsmal")
@XmlAccessorType(XmlAccessType.FIELD)
data class Question(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_av") var answeredBy: Representative? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_av_minister_id") var answeredByMinisterId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_av_minister_tittel") var answeredByMinisterTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_dato") var answeredDate: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av") var answeredFor: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av_minister_id") var answeredForMinisterId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av_minister_tittel") var answeredForMinisterTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "datert_dato") var dated: String? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "emne_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "emne") var sub: List<Topic>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "flyttet_til") var movedTo: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fremsatt_av_annen") var delayedBy: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "rette_vedkommende") var amendedConcerned: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "rette_vedkommende_minister_id") var amendedConcernedMinisterId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "rette_vedkommende_minister_tittel") var amendedConcernedMinisterTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sendt_dato") var sentDate: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporsmal_fra") var questionBy: Representative? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporsmal_nummer") var questionNumber: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporsmal_til") var questionTo: Representative? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporsmal_til_minister_id") var questionToMinisterId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporsmal_til_minister_tittel") var questionToMinisterTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "status") var status: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "type") var type: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "sak")
@XmlAccessorType(XmlAccessType.FIELD)
data class ItemSummary(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "dokumentgruppe") var group: String? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "emne_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "emne") var sub: List<Topic>? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "forslagstiller_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "representant") var suggestedBy: List<Representative>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "henvisning") var reference: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "innstilling_id") var suggestionId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "innstilling_kode") var suggestionCode: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "komite") var committee: Committee? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "korttittel") var shortTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sak_fremmet_id") var supportId: String? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "saksordfoerer_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "representant") var spokesmen: List<Representative>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sist_oppdatert_dato") var lastUpdate: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "status") var state: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "type") var type: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "detaljert_sak")
@XmlAccessorType(XmlAccessType.FIELD)
data class Item(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "dokumentgruppe") var group: String? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "emne_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "emne") var sub: List<Topic>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "ferdigbehandlet") var done: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "henvisning") var reference: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "innstillingstekst") var suggestion: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "komite") var committee: Committee? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "korttittel") var shortTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "kortvedtak") var shortText: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "parentestekst") var additionalText: String? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "publikasjon_referanse_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "publikasjon_referanse") var publications: List<Publication>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sak_nummer") var itemId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sak_opphav") var itemOrigin: ItemOrigin? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "saksgang") var itemProcedure: ItemProcedure? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "saksordfoerer_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "representant") var spokesmen: List<Representative>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "status") var state: String? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "stikkord_liste") var tags: List<String>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "type") var type: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "vedtakstekst") var note: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "publikasjon_referanse")
@XmlAccessorType(XmlAccessType.FIELD)
data class Publication(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "eksport_id") var exportId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "lenke_tekst") var linkText: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "lenke_url") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "type") var type: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "undertype") var subType: String? = null
) : Node

@XmlAccessorType(XmlAccessType.FIELD)
data class ItemOrigin(
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "forslagstiller_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "representant") var spokesmen: List<Representative>? = null
) : Skip {
    override fun value(): Any? {
        return spokesmen
    }

    override fun property(): KProperty<*> {
        return ItemOrigin::spokesmen
    }
}

@XmlRootElement(namespace = STORTINGET_URI, name = "saksgang")
@XmlAccessorType(XmlAccessType.FIELD)
data class ItemProcedure(
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "saksgang_steg_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "saksgang_steg") var step: List<ItemProcedureStep>? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "saksgang_steg")
@XmlAccessorType(XmlAccessType.FIELD)
data class ItemProcedureStep(
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "steg_nummer") var number: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "uaktuell") var current: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "sak_votering")
@XmlAccessorType(XmlAccessType.FIELD)
data class Vote(
        @field:XmlElement(namespace = STORTINGET_URI, name = "alternativ_votering_id") var alternativeVoteId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "antall_for") var votesFor: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "antall_ikke_tilstede") var absent: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "antall_mot") var votesAgainst: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "behandlingsrekkefoelge") var processOrder: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "dagsorden_sak_nummer") var itemId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fri_votering") var freeVote: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "kommentar") var comment: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "mote_kart_nummer") var cardNumber: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "personlig_votering") var personalVote: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "president") var president: Representative? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "vedtatt") var accepted: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_metode") var voteType: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_resultat_type") var voteResult: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_resultat_type_tekst") var voteResultInfo: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_tema") var voteTopic: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_tid") var voteTime: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "voteringsforslag")
@XmlAccessorType(XmlAccessType.FIELD)
data class VoteProposal(
        @field:XmlElement(namespace = STORTINGET_URI, name = "forslag_id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "forslag_betegnelse") var name: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "forslag_betegnelse_kort") var shortName: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "forslag_levert_av_representant") var byRepresentative: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "forslag_paa_vegne_av_tekst") var byText: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "forslag_sorteringsnummer") var number: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "forslag_tekst") var text: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "forslag_type") var type: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "voteringsvedtak")
@XmlAccessorType(XmlAccessType.FIELD)
data class VoteDecision(
        @field:XmlElement(namespace = STORTINGET_URI, name = "vedtak_kode") var code: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "vedtak_kommentar") var comment: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "vedtak_nummer") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "vedtak_referanse") var reference: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "vedtak_tekst") var text: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "representant_voteringsresultat")
@XmlAccessorType(XmlAccessType.FIELD)
data class VoteResult(
        override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "vara_for") var substituteFor: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fast_vara_for") var steadySubstituteFor: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering") var vote: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "representant") var reference: Representative? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "mote")
@XmlAccessorType(XmlAccessType.FIELD)
data class Meeting(
        @field:XmlElement(namespace = STORTINGET_URI, name = "dagsorden_nummer") var number: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fotnote") var footnote: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "ikke_motedag_tekst") var noMeetingText: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "kveldsmote") var eveningMeeting: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "merknad") var note: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "mote_dato_tid") var time: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "mote_rekkefolge") var order: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "mote_ting") var location: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "referat_id") var protocolId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tilleggsdagsorden") var extra: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "dagsordensak")
@XmlAccessorType(XmlAccessType.FIELD)
data class MeetingAgendum(
        override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "dagsordensak_henvisning") var protocolId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "dagsordensak_nummer") var number: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "dagsordensak_tekst") var tekst: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "dagsordensak_type") var type: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fotnote") var footnote: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "innstilling_id") var noIdeaId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "komite_id") var committeeId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "loseforslag") var suggestion: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sak_id") var itemId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporretime_type") var questionType: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporsmal_id") var questionId: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "horing")
@XmlAccessorType(XmlAccessType.FIELD)
data class Hearing(
        @field:XmlElement(namespace = STORTINGET_URI, name = "anmodningsfrist_dato_tid") var protocolId: String? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "horing_sak_info_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "horing_sak_info") var info: List<HearingItemInfo>? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "horingstidspunkt_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "horingstidspunkt") var time: List<HearingTimeInfo>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "komite") var committee: Committee? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "publisert_dato") var publishingDate: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "status") var status: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "status_info_tekst") var statusInfoText: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "type") var type: String? = null
) : Node

@XmlAccessorType(XmlAccessType.FIELD)
data class HearingItemInfo(
        @field:XmlElement(namespace = STORTINGET_URI, name = "sak_henvisning") var reference: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sak_id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sak_tittel") var title: String? = null
) : Node

@XmlAccessorType(XmlAccessType.FIELD)
data class HearingTimeInfo(
        @field:XmlElement(namespace = STORTINGET_URI, name = "tidspunkt") var time: String? = null
) : Skip {
    override fun value(): Any? {
        return time
    }

    override fun property(): KProperty<*> {
        return HearingTimeInfo::time
    }
}

@XmlRootElement(namespace = STORTINGET_URI, name = "horing")
@XmlAccessorType(XmlAccessType.FIELD)
data class HearingProgram(
        override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "dato") var date: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fotnote") var footnote: String? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "horingsprogram_element_liste") @field:XmlElement(namespace = STORTINGET_URI, name = "horingsprogram_element") var element: List<HearingProgramElement>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "innledning") var introduction: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "merknad") var note: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "rom_id") var roomId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sted") var place: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tekst") var tekst: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "video_overforing") var broadcasting: String? = null
) : Node

@XmlAccessorType(XmlAccessType.FIELD)
data class HearingProgramElement(
        override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "rekkefolge_nummer") var order: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tekst") var text: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tidsangivelse") var timeInfo: String? = null
) : Node

interface Consumer<in T : Node> {
    fun onElement(element: T)

    class IdSetting<T : Node>(val owner: Node, val postfix: KProperty<*>) : Consumer<T> {
        override fun onElement(element: T) {
            var current: Node = element
            current.id = owner.id + "-" + postfix.getter.call(current)
        }
    }

    object Printing : Consumer<Node> {
        val logger = LoggerFactory.getLogger(Printing.javaClass)

        override fun onElement(element: Node) {
            logger.info(element.toString())
            element.javaClass.kotlin.declaredMemberProperties.forEach {
                if (it.getter.call(element) == null) {
                    throw IllegalArgumentException("Value not set: $it")
                }
            }
        }
    }

    class GraphWriting(targetPath: File) : Consumer<Node>, Runnable {

        val database = GraphDatabaseFactory().newEmbeddedDatabase(targetPath)

        override fun onElement(element: Node) {
            val transaction = database.beginTx()
            try {
                val query = StringBuilder("MERGE (n:").append(element.javaClass.simpleName).append(" {identifier: {id}}) SET n = {properties} ")
                val properties = HashMap<String, Any?>()
                element.javaClass.kotlin.declaredMemberProperties.filter { it.name == "id" }.forEach {
                    var value = it.get(element)
                    var property: KProperty<*> = it
                    while (value is Skip) {
                        property = value.property()
                        value = value.value()
                    }
                    fun process(value: Any?, placeholder: String, label: String) {
                        when (value) {
                            is Node -> {
                                query.append("MERGE (n)-->(:").append(label).append(" {identifier: {").append(placeholder).append("}}) ")
                                properties.put(placeholder, value.id)
                            }
                            is List<*> -> properties.put(placeholder, value.toTypedArray())
                            else -> properties.put(placeholder, value)
                        }
                    }
                    when (value) {
                        is List<*> -> value.forEachIndexed { i, value -> process(value, property.name + i, value!!.javaClass.simpleName) }
                        else -> process(value, property.name, property.javaField!!.type.simpleName)
                    }
                }
                val parameters = HashMap<String, Any?>()
                parameters.put("id", element.id)
                parameters.put("properties", properties)
                database.execute(query.toString(), parameters)
                transaction.success()
            } catch (exception: Exception) {
                transaction.failure()
                exception.printStackTrace()
            } finally {
                transaction.close()
            }
        }

        override fun run() {
            database.shutdown()
        }
    }
}

interface Dispatcher {
    fun <T : Node> apply(parser: ThrottledXmlParser<T>, consumers: Array<out Consumer<T>>)

    fun endOfScript(startTime: Date) {
        val endTime = Date()
        val difference = endTime.time - startTime.time
        LoggerFactory.getLogger(Dispatcher::class.java)
                .info("Finished parsing after ${difference / (1000 * 60)}:${difference / 1000}: ${SimpleDateFormat("HH:mm").format(endTime)}")
    }

    object Synchronous : Dispatcher {
        override fun <T : Node> apply(parser: ThrottledXmlParser<T>, consumers: Array<out Consumer<T>>) {
            parser.doRead(consumers)
        }
    }

    class Asynchronous(val startTime: Date, shutDown: Runnable,
                       val executor: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) : Dispatcher, Runnable {

        private val semaphore = ShutdownMonitor(this, shutDown)

        override fun <T : Node> apply(parser: ThrottledXmlParser<T>, consumers: Array<out Consumer<T>>) {
            semaphore.increment()
            executor.execute(Job(semaphore, parser, consumers))
        }

        override fun endOfScript(startTime: Date) {
            semaphore.decrement()
        }

        override fun run() {
            executor.shutdown()
            super.endOfScript(startTime)
        }

        private class Job<T : Node>(val semaphore: ShutdownMonitor, val parser: ThrottledXmlParser<T>, val consumers: Array<out Consumer<T>>) : Runnable {
            override fun run() {
                try {
                    parser.doRead(consumers)
                } finally {
                    semaphore.decrement()
                }
            }
        }

        private class ShutdownMonitor(vararg val listeners: Runnable) {
            val counter = AtomicInteger(1)

            fun increment(): Unit {
                counter.incrementAndGet()
            }

            fun decrement(): Unit {
                if (counter.decrementAndGet() == 0) {
                    listeners.forEach { it.run() }
                }
            }
        }
    }
}

class ThrottledXmlParser<T : Node>(val endpoint: String, type: Class<out T>) {
    private val logger = LoggerFactory.getLogger(ThrottledXmlParser::class.java)
    private val unmarshaller = JAXBContext.newInstance(type).createUnmarshaller()
    private val tag = type.getAnnotation(XmlRootElement::class.java).name

    fun read(dispatcher: Dispatcher, vararg consumers: Consumer<T>) {
        logger.info("Parsing $EXPORT_URI$endpoint")
        dispatcher.apply(this, consumers)
    }

    fun doRead(consumers: Array<out Consumer<T>>) {
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

private fun readAll(dispatcher: Dispatcher, defaultConsumer: Consumer<Node>) {
    ThrottledXmlParser("allekomiteer", Committee::class.java).read(dispatcher, defaultConsumer)
    ThrottledXmlParser("allepartier", Party::class.java).read(dispatcher, defaultConsumer)
    ThrottledXmlParser("fylker", Area::class.java).read(dispatcher, defaultConsumer)
    ThrottledXmlParser("emner", Topic::class.java).read(dispatcher, defaultConsumer)
    ThrottledXmlParser("saksganger", ItemProcedure::class.java).read(dispatcher, defaultConsumer)
    ThrottledXmlParser("stortingsperioder", Period::class.java).read(dispatcher, defaultConsumer, object : Consumer<Period> {
        override fun onElement(element: Period) {
            ThrottledXmlParser("representanter?stortingsperiodeid=${element.id}", Representative::class.java).read(dispatcher, defaultConsumer)
        }
    })
    ThrottledXmlParser("sesjoner", Session::class.java).read(dispatcher, defaultConsumer, object : Consumer<Session> {
        override fun onElement(element: Session) {
            ThrottledXmlParser("komiteer?sesjonid=${element.id}", Committee::class.java).read(dispatcher, defaultConsumer)
            ThrottledXmlParser("partier?sesjonid=${element.id}", Party::class.java).read(dispatcher, defaultConsumer)
            ThrottledXmlParser("sporretimesporsmal?sesjonid=${element.id}", Question::class.java).read(dispatcher, defaultConsumer)
            ThrottledXmlParser("interpellasjoner?sesjonid=${element.id}", Question::class.java).read(dispatcher, defaultConsumer)
            ThrottledXmlParser("skriftligesporsmal?sesjonid=${element.id}", Question::class.java).read(dispatcher, defaultConsumer)
            ThrottledXmlParser("horinger?sesjonid=${element.id}", Hearing::class.java).read(dispatcher, defaultConsumer, object : Consumer<Hearing> {
                override fun onElement(element: Hearing) {
                    ThrottledXmlParser("horingsprogram?horingid=${element.id}", HearingProgram::class.java).read(dispatcher,
                            Consumer.IdSetting(element, HearingProgram::date),
                            object : Consumer<HearingProgram> {
                                override fun onElement(element: HearingProgram) {
                                    element.element?.forEach { it.id = element.id + "-" + it.order }
                                }
                            }, defaultConsumer)
                }
            })
            ThrottledXmlParser("moter?sesjonid=${element.id}", Meeting::class.java).read(dispatcher, defaultConsumer, object : Consumer<Meeting> {
                override fun onElement(element: Meeting) {
                    if (element.id != "-1") {
                        ThrottledXmlParser("dagsorden?moteid=${element.id}", MeetingAgendum::class.java).read(dispatcher,
                                Consumer.IdSetting(element, MeetingAgendum::number),
                                defaultConsumer)
                    }
                }
            })
            ThrottledXmlParser("saker?sesjonid=${element.id}", ItemSummary::class.java).read(dispatcher, defaultConsumer, object : Consumer<ItemSummary> {
                override fun onElement(element: ItemSummary) {
                    ThrottledXmlParser("sak?sakid=${element.id}", Item::class.java).read(dispatcher, defaultConsumer)
                    ThrottledXmlParser("voteringer?sakid=${element.id}", Vote::class.java).read(dispatcher, defaultConsumer, object : Consumer<Vote> {
                        override fun onElement(element: Vote) {
                            ThrottledXmlParser("voteringsforslag?voteringid=${element.id}", VoteProposal::class.java).read(dispatcher,
                                    Consumer.IdSetting(element, VoteProposal::number),
                                    defaultConsumer)
                            ThrottledXmlParser("voteringsvedtak?voteringid=${element.id}", VoteDecision::class.java).read(dispatcher,
                                    Consumer.IdSetting(element, VoteDecision::code),
                                    defaultConsumer)
                            ThrottledXmlParser("voteringsresultat?voteringid=${element.id}", VoteResult::class.java).read(dispatcher,
                                    Consumer.IdSetting(element, VoteResult::reference),
                                    defaultConsumer)
                        }
                    })
                }
            })
        }
    })
}

fun main(args: Array<String>) {
    val dispatcher: Dispatcher
    val defaultConsumer: Consumer<Node>
    val startTime = Date()
    if (args.isEmpty()) {
        dispatcher = Dispatcher.Synchronous
        defaultConsumer = Consumer.Printing
    } else if (args.size == 1) {
        val targetPath = File(args[0])
        if (targetPath.listFiles().isNotEmpty()) {
            throw IllegalArgumentException("Not empty: $targetPath")
        } else if (!targetPath.isDirectory || !targetPath.canRead() || !targetPath.canWrite()) {
            throw IllegalArgumentException("Cannot read/write or not a folder: $targetPath")
        }
        defaultConsumer = Consumer.GraphWriting(targetPath)
        dispatcher = Dispatcher.Asynchronous(startTime, defaultConsumer)
    } else {
        throw IllegalArgumentException("Illegal arguments: $args")
    }
    LoggerFactory.getLogger(Dispatcher::class.java).info("Begin parsing: ${SimpleDateFormat("HH:mm:ss").format(startTime)}")
    readAll(dispatcher, defaultConsumer)
    dispatcher.endOfScript(startTime)
}
