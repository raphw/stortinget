package no.blogspot.mydailyjava

import org.neo4j.graphdb.DynamicLabel
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.kernel.DeadlockDetectedException
import org.reflections.Reflections
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
import javax.xml.bind.annotation.adapters.XmlAdapter
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement
import kotlin.reflect.KClass
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

class DateAdapter : XmlAdapter<String, Date>() {

    private val placeholder = "0001-01-01T00:00:00"

    private val format = "yyyy-MM-dd'T'HH:mm:ss"

    override fun unmarshal(value: String?): Date? {
        return if (value == placeholder) null else SimpleDateFormat(format).parse(value)
    }

    override fun marshal(value: Date?): String? {
        return if (value == null) placeholder else SimpleDateFormat(format).format(value)
    }
}

enum class Gender(private val id: String) {

    MALE("mann"),
    FEMALE("kvinne");

    class Adapter : XmlAdapter<String, Gender>() {
        override fun marshal(value: Gender?): String? {
            return value?.id
        }

        override fun unmarshal(value: String?): Gender? {
            return values().filter { it.id == value }.firstOrNull()
        }
    }
}

enum class QuestionState(private val id: String) {

    ANSWERED("besvart"),
    UNSPECIFIED("ikke_spesifisert"),
    DISCARDED("bortfalt"),
    UNDER_DISCUSSION("til_behandling"),
    WITHDRAWN("trukket"),
    WAITING("venter_utsatt");

    class Adapter : XmlAdapter<String, QuestionState>() {
        override fun marshal(value: QuestionState?): String? {
            return value?.id
        }

        override fun unmarshal(value: String?): QuestionState? {
            return values().filter { it.id == value }.firstOrNull()
        }
    }
}

enum class QuestionResponsible(private val id: String) {

    UNSPECIFIED("ikke_spesifisert"),
    ADRESSED("rette_vedkommende"),
    MINISTER("settestatsrad");

    class Adapter : XmlAdapter<String, QuestionResponsible>() {
        override fun marshal(value: QuestionResponsible?): String? {
            return value?.id
        }

        override fun unmarshal(value: String?): QuestionResponsible? {
            return values().filter { it.id == value }.firstOrNull()
        }
    }
}

enum class QuestionType(private val id: String) {

    ORAL("muntlig_sporsmal"),
    WRITTEN("skriftlig_sporsmal"),
    INTERPELLATION("interpellasjon"),
    TO_PRESIDENCY("til_presidentskapet"),
    CLOSING("ved_motets_slutt"),
    QUESTION_TIME("sporretime_sporsmal");

    class Adapter : XmlAdapter<String, QuestionType>() {
        override fun marshal(value: QuestionType?): String? {
            return value?.id
        }

        override fun unmarshal(value: String?): QuestionType? {
            return values().filter { it.id == value }.firstOrNull()
        }
    }
}

enum class SuggestionCode(private val id: String) {

    STANDARD("innstilling_s"),
    LOVEVEDTAK("innstilling_i");

    class Adapter : XmlAdapter<String, SuggestionCode>() {
        override fun marshal(value: SuggestionCode?): String? {
            return value?.id
        }

        override fun unmarshal(value: String?): SuggestionCode? {
            return values().filter { it.id == value }.firstOrNull()
        }
    }
}

enum class ItemType(private val id: String) {

    BUDGET("budsjett"),
    LAW("lovsak"),
    GENERAL("alminneligsak");

    class Adapter : XmlAdapter<String, ItemType>() {
        override fun marshal(value: ItemType?): String? {
            return value?.id
        }

        override fun unmarshal(value: String?): ItemType? {
            return values().filter { it.id == value }.firstOrNull()
        }
    }
}

enum class ItemStatus(private val id: String) {

    ANNOUNCED("varslet"),
    INCOMING("mottatt"),
    IN_PROCESS("til_behandling"),
    PROCESSED("behandlet"),
    WITHDRAWN("trukket"),
    DISCARDED("bortfalt");

    class Adapter : XmlAdapter<String, ItemStatus>() {
        override fun marshal(value: ItemStatus?): String? {
            return value?.id
        }

        override fun unmarshal(value: String?): ItemStatus? {
            return values().filter { it.id == value }.firstOrNull()
        }
    }
}

enum class ItemDocumentGroup(private val id: String) {

    PROPOSITION("proposisjon"),
    NOTE("melding"),
    REPORT("redegjoerelse"),
    REPRESENTATIVE_SUGGESTION("representantforslag"),
    DOCUMENT_SERIES("dokumentserien"),
    SUGGESTION("innstillingssaker"),
    DELIVERY("innberetning");

    class Adapter : XmlAdapter<String, ItemDocumentGroup>() {
        override fun marshal(value: ItemDocumentGroup?): String? {
            return value?.id
        }

        override fun unmarshal(value: String?): ItemDocumentGroup? {
            return values().filter { it.id == value }.firstOrNull()
        }
    }
}

enum class PublicationType(private val id: String) {

    UNSPECIFIED("ikke_spesifisert"),
    GOVERNMENT("regjering"),
    DOK3("dok3"),
    DOK8("dok8"),
    DOK12("dok12"),
    DOCUMENT_SERIES("dokumentserie"),
    DELIVERY("innberetning"),
    SUGGESTION("innstilling"),
    DECISION("vedtak"),
    LAW("lovvedtak"),
    LAW_COMMENT("lovanmerkning"),
    REPORT("referat");

    class Adapter : XmlAdapter<String, PublicationType>() {
        override fun marshal(value: PublicationType?): String? {
            return value?.id
        }

        override fun unmarshal(value: String?): PublicationType? {
            return values().filter { it.id == value }.firstOrNull()
        }
    }
}

enum class VotingType(private val id: String) {

    ELECTRONIC("elektronisk"),
    BY_NAME("navneopprop"),
    SITTING("staaende_sittende"),
    WRITTEN("skriftlig");

    class Adapter : XmlAdapter<String, VotingType>() {
        override fun marshal(value: VotingType?): String? {
            return value?.id
        }

        override fun unmarshal(value: String?): VotingType? {
            return values().filter { it.id == value }.firstOrNull()
        }
    }
}

enum class VotingResultType(private val id: String) {

    MANUALLY("manuell"),
    UNANIMOUS("enstemmig_vedtatt"),
    ACCEPTED("vedtatt_mot_N_stemmer"),
    REJECTED("forkastet_mot_N_stemmer"),
    ACCEPTED_PRESIDENT_VOTE("vedtatt_med_president_dobbeltstemme"),
    REJECTED_PRESIDENT_VOTE("forkastet_med_president_dobbeltstemme");

    class Adapter : XmlAdapter<String, VotingResultType>() {
        override fun marshal(value: VotingResultType?): String? {
            return value?.id
        }

        override fun unmarshal(value: String?): VotingResultType? {
            return values().filter { it.id == value }.firstOrNull()
        }
    }
}

@Retention(AnnotationRetention.RUNTIME)
annotation class LinkTo(val target: KClass<out Node>)

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
        @field:XmlElement(namespace = STORTINGET_URI, name = "fra") @field:XmlJavaTypeAdapter(DateAdapter::class) var from: Date? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "til") @field:XmlJavaTypeAdapter(DateAdapter::class) var to: Date? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "emne")
@XmlAccessorType(XmlAccessType.FIELD)
data class Topic(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "er_hovedemne") var main: Boolean? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "hovedemne_id") @LinkTo(Topic::class) var mainId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "emne") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "underemne_liste") var subTopic: List<Topic>? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "representant")
@XmlAccessorType(XmlAccessType.FIELD)
data class Representative(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "foedselsdato") @field:XmlJavaTypeAdapter(DateAdapter::class) var birth: Date? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "doedsdato") @field:XmlJavaTypeAdapter(DateAdapter::class) var death: Date? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fornavn") var firstName: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "etternavn") var lastName: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "kjoenn") @field:XmlJavaTypeAdapter(Gender.Adapter::class) var gender: Gender? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fylke") var area: Area? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "parti") var party: Party? = null
) : Node /// TODO: "dagensrepresentant"

@XmlRootElement(namespace = STORTINGET_URI, name = "sesjon")
@XmlAccessorType(XmlAccessType.FIELD)
data class Session(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fra") @field:XmlJavaTypeAdapter(DateAdapter::class) var from: Date? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "til") @field:XmlJavaTypeAdapter(DateAdapter::class) var to: Date? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "sporsmal")
@XmlAccessorType(XmlAccessType.FIELD)
data class Question(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_av") var answeredBy: Representative? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_av_minister_id") @LinkTo(Representative::class) var answeredByMinisterId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_av_minister_tittel") var answeredByMinisterTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_dato") @field:XmlJavaTypeAdapter(DateAdapter::class) var answeredDate: Date? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av") var answeredFor: Representative? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av_minister_id") @LinkTo(Representative::class) var answeredForMinisterId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av_minister_tittel") var answeredForMinisterTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "datert_dato") @field:XmlJavaTypeAdapter(DateAdapter::class) var dated: Date? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "emne") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "emne_liste") var topic: List<Topic>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "flyttet_til") @field:XmlJavaTypeAdapter(QuestionResponsible.Adapter::class) var responsible: QuestionResponsible? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fremsatt_av_annen") var delayedBy: Representative? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "rette_vedkommende") var amendedConcerned: Representative? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "rette_vedkommende_minister_id") @LinkTo(Representative::class) var amendedConcernedMinisterId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "rette_vedkommende_minister_tittel") var amendedConcernedMinisterTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sendt_dato") @field:XmlJavaTypeAdapter(DateAdapter::class) var sentDate: Date? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporsmal_fra") var questionBy: Representative? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporsmal_nummer") var number: Int? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporsmal_til") var questionTo: Representative? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporsmal_til_minister_id") @LinkTo(Representative::class) var questionToMinisterId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sporsmal_til_minister_tittel") var questionToMinisterTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "status") @field:XmlJavaTypeAdapter(QuestionState.Adapter::class) var status: QuestionState? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "type") @field:XmlJavaTypeAdapter(QuestionType.Adapter::class) var type: QuestionType? = null
) : Node // TODO: Representative = Person?

@XmlRootElement(namespace = STORTINGET_URI, name = "sak")
@XmlAccessorType(XmlAccessType.FIELD)
data class ItemSummary(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "dokumentgruppe") @field:XmlJavaTypeAdapter(ItemDocumentGroup.Adapter::class) var documentGroup: ItemDocumentGroup? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "emne") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "emne_liste") var topic: List<Topic>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "representant") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "forslagstiller_liste") var proponent: List<Representative>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "henvisning") var reference: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "innstilling_id") @LinkTo(Item::class) var suggestion: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "innstilling_kode") @XmlJavaTypeAdapter(SuggestionCode.Adapter::class) var suggestionCode: SuggestionCode? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "komite") var committee: Committee? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "korttittel") var shortTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sak_fremmet_id") @LinkTo(Item::class) var supportedBy: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "representant") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "saksordfoerer_liste") var spokesman: List<Representative>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sist_oppdatert_dato") @XmlJavaTypeAdapter(DateAdapter::class) var lastUpdate: Date? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "status") @field:XmlJavaTypeAdapter(ItemStatus.Adapter::class) var state: ItemStatus? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "type") @field:XmlJavaTypeAdapter(ItemType.Adapter::class) var type: ItemType? = null
) : Node // TODO: instilling_id -> links to what?

@XmlRootElement(namespace = STORTINGET_URI, name = "detaljert_sak")
@XmlAccessorType(XmlAccessType.FIELD)
data class Item(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "dokumentgruppe") @field:XmlJavaTypeAdapter(ItemDocumentGroup.Adapter::class) var documentGroup: ItemDocumentGroup? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "emne") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "emne_liste") var topic: List<Topic>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "ferdigbehandlet") var done: Boolean? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "henvisning") var reference: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "innstillingstekst") var suggestion: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "komite") var committee: Committee? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "korttittel") var shortTitle: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "kortvedtak") var shortText: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "parentestekst") var additionalText: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "publikasjon_referanse") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "publikasjon_referanse_liste") var publication: List<Publication>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sak_nummer") var number: Int? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sak_opphav") var itemOrigin: ItemOrigin? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "saksgang") var itemProcedure: ItemProcedure? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "representant") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "saksordfoerer_liste")var spokesman: List<Representative>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "status") @field:XmlJavaTypeAdapter(ItemStatus.Adapter::class) var state: ItemStatus? = null,
        @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "stikkord_liste") var tags: List<String>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "type") @field:XmlJavaTypeAdapter(ItemType.Adapter::class) var type: ItemType? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "vedtakstekst") var note: String? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "publikasjon_referanse")
@XmlAccessorType(XmlAccessType.FIELD)
data class Publication(
        @field:XmlElement(namespace = STORTINGET_URI, name = "versjon") var version: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "eksport_id") var export: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "lenke_tekst") var linkText: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "lenke_url") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "type") @field:XmlJavaTypeAdapter(PublicationType.Adapter::class) var type: PublicationType? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "undertype") var subType: String? = null
) : Node // TODO: Export id

@XmlAccessorType(XmlAccessType.FIELD)
data class ItemOrigin(
        @field:XmlElement(namespace = STORTINGET_URI, name = "representant") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "forslagstiller_liste") var originator: List<Representative>? = null
) : Skip {
    override fun value(): Any? {
        return originator
    }

    override fun property(): KProperty<*> {
        return ItemOrigin::originator
    }
}

@XmlRootElement(namespace = STORTINGET_URI, name = "saksgang")
@XmlAccessorType(XmlAccessType.FIELD)
data class ItemProcedure(
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "saksgang_steg") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "saksgang_steg_liste") var step: List<ItemProcedureStep>? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "saksgang_steg")
@XmlAccessorType(XmlAccessType.FIELD)
data class ItemProcedureStep(
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "steg_nummer") var number: Int? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "uaktuell") var current: Boolean? = null
) : Node

@XmlRootElement(namespace = STORTINGET_URI, name = "sak_votering")
@XmlAccessorType(XmlAccessType.FIELD)
data class Vote(
        @field:XmlElement(namespace = STORTINGET_URI, name = "alternativ_votering_id") var alternativeVoteId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "antall_for") var votesFor: Int? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "antall_ikke_tilstede") var absent: Int? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "antall_mot") var votesAgainst: Int? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "behandlingsrekkefoelge") var order: Int? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "fri_votering") var freeVote: Boolean? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "kommentar") var comment: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "mote_kart_nummer") var cardNumber: Int? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "personlig_votering") var personalVote: Boolean? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "president") var president: Representative? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "vedtatt") var accepted: Boolean? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_metode") @field:XmlJavaTypeAdapter(VotingType.Adapter::class) var votingType: VotingType? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_resultat_type") @field:XmlJavaTypeAdapter(VotingResultType.Adapter::class) var votingResultType: VotingResultType? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_resultat_type_tekst") var voteResultInfo: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_tema") var voteTopic: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "votering_tid") @field:XmlJavaTypeAdapter(DateAdapter::class) var voteTime:Date? = null
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
        @field:XmlElement(namespace = STORTINGET_URI, name = "anmodningsfrist_dato_tid") @XmlJavaTypeAdapter(DateAdapter::class) var appealDeadline: Date? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "horing_sak_info") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "horing_sak_info_liste") var info: List<HearingItemInfo>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "horingstidspunkt") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "horingstidspunkt_liste") var time: List<HearingTimeInfo>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "id") override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "komite") var committee: Committee? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "publisert_dato") @XmlJavaTypeAdapter(DateAdapter::class) var publishingDate: Date? = null,
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
        @field:XmlElement(namespace = STORTINGET_URI, name = "horingsprogram_element") @field:XmlElementWrapper(namespace = STORTINGET_URI, name = "horingsprogram_element_liste") var element: List<HearingProgramElement>? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "innledning") var introduction: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "merknad") var note: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "rom_id") var roomId: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "sted") var place: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tekst") var tekst: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "tittel") var title: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "video_overforing") var broadcasting: Boolean? = null
) : Node

@XmlAccessorType(XmlAccessType.FIELD)
data class HearingProgramElement(
        override var id: String? = null,
        @field:XmlElement(namespace = STORTINGET_URI, name = "rekkefolge_nummer") var order: Int? = null,
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

    interface Linkable<in S : Node> : Consumer<S> {

        fun linkTo(parent: Node, name: String): Consumer<S>
    }

    object Printing : Consumer.Linkable<Node> {
        val logger = LoggerFactory.getLogger(Printing.javaClass)

        override fun onElement(element: Node) {
            logger.info(element.toString())
            element.javaClass.kotlin.declaredMemberProperties.forEach {
                if (it.getter.call(element) == null) {
                    logger.debug("Missing value ${it.name} for $element")
                }
            }
        }

        override fun linkTo(parent: Node, name: String): Consumer<Node> {
            return this
        }
    }

    class GraphWriting(targetPath: File) : Consumer.Linkable<Node>, Runnable {

        private val database = GraphDatabaseFactory().newEmbeddedDatabase(targetPath)

        private val logger = LoggerFactory.getLogger(GraphWriting::class.java)

        init {
            val transaction = database.beginTx()
            try {
                Reflections(Node::class.java.`package`.name)
                        .getSubTypesOf(Node::class.java)
                        .forEach {
                            logger.info("Creating index for ${it.simpleName}")
                            database.schema().indexFor(DynamicLabel.label(it.simpleName)).on("id").create()
                        }
            } catch(exception: Exception) {
                transaction.failure()
                logger.error("Could not create indices", exception)
            } finally {
                transaction.close()
            }
        }

        private fun String.toCamelCase(): String {
            return this.replace("([a-z])([A-Z]+)", "$1_$2").toUpperCase(Locale.US)
        }

        private fun transactionalWrite(element: Any, doWrite: () -> Unit, maxAttempts: Int = 20): Unit {
            var attempt = 0
            while (attempt++ < maxAttempts) {
                val transaction = database.beginTx()
                try {
                    doWrite()
                    transaction.success()
                    return
                } catch (exception: DeadlockDetectedException) {
                    transaction.failure()
                    logger.debug("Dead lock on inserting $element", exception)
                } catch (exception: Exception) {
                    transaction.failure()
                    logger.error("Could not insert $element", exception)
                    return
                } finally {
                    transaction.close()
                }
            }
            logger.error("Too many deadlocks during insert of $element ($maxAttempts)")
        }

        override fun onElement(element: Node) {
            transactionalWrite(element, {
                val query = StringBuilder("MERGE (n:${element.javaClass.simpleName} {id: {id}}) SET n += {properties}")
                val parameters = HashMap<String, Any?>()
                parameters.put("id", element.id)
                val properties = HashMap<String, Any?>()
                parameters.put("properties", properties)
                element.javaClass.kotlin.declaredMemberProperties.filter { it.name != "id" }.forEach {
                    var value = it.get(element)
                    var property: KProperty<*> = it
                    while (value is Skip) {
                        property = value.property()
                        value = value.value()
                    }
                    val linkTarget = it.annotations.filter { it.annotationClass == LinkTo::class }.firstOrNull() as LinkTo?
                    fun process(value: Any?, name: String, label: Class<*>, index: Int = -1) {
                        fun createLink(id: String, label: String) {
                            val identifier = if (index == -1) name else name + index
                            query.append(" MERGE ($identifier:$label {id: {$identifier}}) CREATE (n)-[:${name.toCamelCase()}]->($identifier)")
                            parameters.put(identifier, id)
                        }
                        when (value) {
                            linkTarget != null -> createLink(value.toString(), linkTarget!!.target.simpleName!!)
                            is Node -> {
                                val id = value.id
                                if (id != null) {
                                    createLink(id, label.simpleName)
                                } else {
                                    logger.debug("Incomplete link $name ('$index') for $element")
                                }
                            }
                            is List<*> -> properties.put(name, value.toTypedArray())
                            else -> properties.put(name, value)
                        }
                    }
                    when (value) {
                        is List<*> -> value.forEachIndexed {
                            index, value -> process(if (value is Skip) value.value() else value, property.name, value!!.javaClass, index)
                        }
                        else -> process(value, property.name, property.javaField!!.type)
                    }
                }
                database.execute(query.toString(), parameters)
            })
        }

        override fun linkTo(parent: Node, name: String): Consumer<Node> {
            return Linking(parent, name)
        }

        override fun run() {
            database.shutdown()
        }

        private inner class Linking(val parent: Node, val name: String) : Consumer<Node> {

            override fun onElement(element: Node) {
                transactionalWrite(element, {
                    database.execute("MERGE (source:${element.javaClass.simpleName}{id: {source}}) " +
                            "MERGE (target:${parent.javaClass.simpleName} {id: {target}}) " +
                            "CREATE (source)-[:${name.toCamelCase()}]->(target)",
                            mapOf("source" to element.id, "target" to parent.id))
                })
            }
        }
    }
}

interface Dispatcher {
    fun <T : Node> apply(parser: ThrottledXmlParser<T>, consumers: Array<out Consumer<T>>)

    fun endOfScript(startTime: Date) {
        val endTime = Date()
        val difference = endTime.time - startTime.time
        LoggerFactory.getLogger(Dispatcher::class.java)
                .info("Finished: ${SimpleDateFormat("HH:mm:ss").format(endTime)} (took ${difference / (1000 * 60)} minutes, ${difference / 1000} seconds)")
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

    fun doRead(consumers: Array<out Consumer<T>>, maxAttempts: Int = 15) {
        var attempt = 0
        while (attempt++ < maxAttempts) {
            try {
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
                                    logger.error("Unexpected data read from $endpoint")
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
                return
            } catch(exception: Exception) {
                logger.debug("Error reading file from $endpoint", exception)
            }
        }
    }
}

private fun readAll(dispatcher: Dispatcher, defaultConsumer: Consumer.Linkable<Node>) {
    ThrottledXmlParser("allekomiteer", Committee::class.java).read(dispatcher, defaultConsumer)
    ThrottledXmlParser("allepartier", Party::class.java).read(dispatcher, defaultConsumer)
    ThrottledXmlParser("fylker", Area::class.java).read(dispatcher, defaultConsumer)
    ThrottledXmlParser("emner", Topic::class.java).read(dispatcher, defaultConsumer)
    ThrottledXmlParser("saksganger", ItemProcedure::class.java).read(dispatcher, defaultConsumer)
    ThrottledXmlParser("stortingsperioder", Period::class.java).read(dispatcher, defaultConsumer, object : Consumer<Period> {
        override fun onElement(element: Period) {
            ThrottledXmlParser("representanter?stortingsperiodeid=${element.id}", Representative::class.java).read(dispatcher,
                    defaultConsumer.linkTo(element, "elected"))
        }
    })
    ThrottledXmlParser("sesjoner", Session::class.java).read(dispatcher, defaultConsumer, object : Consumer<Session> {
        override fun onElement(element: Session) {
            ThrottledXmlParser("komiteer?sesjonid=${element.id}", Committee::class.java).read(dispatcher,
                    defaultConsumer.linkTo(element, "conducted"))
            ThrottledXmlParser("partier?sesjonid=${element.id}", Party::class.java).read(dispatcher,
                    defaultConsumer.linkTo(element, "represented"))
            ThrottledXmlParser("sporretimesporsmal?sesjonid=${element.id}", Question::class.java).read(dispatcher,
                    defaultConsumer.linkTo(element, "asked"))
            ThrottledXmlParser("interpellasjoner?sesjonid=${element.id}", Question::class.java).read(dispatcher,
                    defaultConsumer.linkTo(element, "asked"))
            ThrottledXmlParser("skriftligesporsmal?sesjonid=${element.id}", Question::class.java).read(dispatcher,
                    defaultConsumer.linkTo(element, "asked"))
            ThrottledXmlParser("horinger?sesjonid=${element.id}", Hearing::class.java).read(dispatcher,
                    defaultConsumer.linkTo(element, "heard"),
                    object : Consumer<Hearing> {
                override fun onElement(element: Hearing) {
                    ThrottledXmlParser("horingsprogram?horingid=${element.id}", HearingProgram::class.java).read(dispatcher,
                            Consumer.IdSetting(element, HearingProgram::date),
                            object : Consumer<HearingProgram> {
                                override fun onElement(element: HearingProgram) {
                                    element.element?.forEach { it.id = element.id + "-" + it.order }
                                }
                            }, defaultConsumer.linkTo(element, "partOf"))
                }
            })
            ThrottledXmlParser("moter?sesjonid=${element.id}", Meeting::class.java).read(dispatcher,
                    defaultConsumer.linkTo(element, "held"),
                    object : Consumer<Meeting> {
                override fun onElement(element: Meeting) {
                    if (element.id != "-1") {
                        ThrottledXmlParser("dagsorden?moteid=${element.id}", MeetingAgendum::class.java).read(dispatcher,
                                Consumer.IdSetting(element, MeetingAgendum::number),
                                defaultConsumer.linkTo(element, "partOf"))
                    }
                }
            })
            ThrottledXmlParser("saker?sesjonid=${element.id}", ItemSummary::class.java).read(dispatcher,
                    defaultConsumer.linkTo(element, "discussed"),
                    object : Consumer<ItemSummary> {
                override fun onElement(element: ItemSummary) {
                    ThrottledXmlParser("sak?sakid=${element.id}", Item::class.java).read(dispatcher,
                            defaultConsumer.linkTo(element, "summarizedBy"))
                    ThrottledXmlParser("voteringer?sakid=${element.id}", Vote::class.java).read(dispatcher,
                            defaultConsumer.linkTo(element, "heldFor"),
                            object : Consumer<Vote> {
                        override fun onElement(element: Vote) {
                            ThrottledXmlParser("voteringsforslag?voteringid=${element.id}", VoteProposal::class.java).read(dispatcher,
                                    Consumer.IdSetting(element, VoteProposal::number),
                                    defaultConsumer.linkTo(element, "proposedFor"))
                            ThrottledXmlParser("voteringsvedtak?voteringid=${element.id}", VoteDecision::class.java).read(dispatcher,
                                    Consumer.IdSetting(element, VoteDecision::code),
                                    defaultConsumer.linkTo(element, "carriedInto"))
                            ThrottledXmlParser("voteringsresultat?voteringid=${element.id}", VoteResult::class.java).read(dispatcher,
                                    Consumer.IdSetting(element, VoteResult::reference),
                                    defaultConsumer.linkTo(element, "decided"))
                        }
                    })
                }
            })
        }
    })
}

fun main(args: Array<String>) {
    val dispatcher: Dispatcher
    val defaultConsumer: Consumer.Linkable<Node>
    val startTime = Date()
    LoggerFactory.getLogger(Dispatcher::class.java).info("Start: ${SimpleDateFormat("HH:mm:ss").format(startTime)}")
    if (args.isEmpty()) {
        dispatcher = Dispatcher.Synchronous
        defaultConsumer = Consumer.Printing
    } else if (args.size == 1) {
        val targetPath = File(args[0])
        if (targetPath.listFiles().isNotEmpty()) {
            throw IllegalArgumentException("Not empty: $targetPath")
        } else if (!targetPath.isDirectory || !targetPath.canWrite()) {
            throw IllegalArgumentException("Cannot write to folder or not a folder at all: $targetPath")
        }
        defaultConsumer = Consumer.GraphWriting(targetPath)
        dispatcher = Dispatcher.Asynchronous(startTime, defaultConsumer)
    } else {
        throw IllegalArgumentException("Illegal arguments: $args")
    }
    readAll(dispatcher, defaultConsumer)
    dispatcher.endOfScript(startTime)
}
