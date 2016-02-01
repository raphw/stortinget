package no.blogspot.mydailyjava

import java.net.URL
import javax.xml.bind.JAXBContext
import javax.xml.bind.annotation.*
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement

const val STORTINGET_URI = "http://data.stortinget.no"
const val EXPORT_URI = "https://data.stortinget.no/eksport/"

@XmlRootElement(namespace = STORTINGET_URI, name = "komite")
@XmlAccessorType(XmlAccessType.FIELD)
data class Committee(@XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
                     @XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null)

@XmlRootElement(namespace = STORTINGET_URI, name = "parti")
@XmlAccessorType(XmlAccessType.FIELD)
data class Party(@XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
                 @XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null)

@XmlRootElement(namespace = STORTINGET_URI, name = "fylke")
@XmlAccessorType(XmlAccessType.FIELD)
data class Area(@XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
                @XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null)

@XmlRootElement(namespace = STORTINGET_URI, name = "stortingsperiode")
@XmlAccessorType(XmlAccessType.FIELD)
data class Period(@XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
                  @XmlElement(namespace = STORTINGET_URI, name = "fra") var from: String? = null,
                  @XmlElement(namespace = STORTINGET_URI, name = "til") var to: String? = null)

@XmlRootElement(namespace = STORTINGET_URI, name = "emne")
@XmlAccessorType(XmlAccessType.FIELD)
data class Topic(@XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
                 @XmlElement(namespace = STORTINGET_URI, name = "er_hovedemne") var main: Boolean? = null,
                 @XmlElement(namespace = STORTINGET_URI, name = "hovedemne_id") var mainId: String? = null,
                 @XmlElement(namespace = STORTINGET_URI, name = "navn") var name: String? = null,
                 @XmlElementWrapper(namespace = STORTINGET_URI, name = "underemne_liste") @XmlElement(namespace = STORTINGET_URI, name = "emne") var sub: List<Topic>? = null)

@XmlRootElement(namespace = STORTINGET_URI, name = "representant")
@XmlAccessorType(XmlAccessType.FIELD)
data class Representative(@XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
                          @XmlElement(namespace = STORTINGET_URI, name = "foedselsdato") var birth: String? = null,
                          @XmlElement(namespace = STORTINGET_URI, name = "doedsdato") var death: String? = null,
                          @XmlElement(namespace = STORTINGET_URI, name = "fornavn") var firstName: String? = null,
                          @XmlElement(namespace = STORTINGET_URI, name = "etternavn") var lastName: String? = null,
                          @XmlElement(namespace = STORTINGET_URI, name = "kjoenn") var gender: String? = null,
                          @XmlElement(namespace = STORTINGET_URI, name = "fylke") var area: Area? = null,
                          @XmlElement(namespace = STORTINGET_URI, name = "parti") var party: Party? = null)

@XmlRootElement(namespace = STORTINGET_URI, name = "sesjon")
@XmlAccessorType(XmlAccessType.FIELD)
data class Session(@XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
                   @XmlElement(namespace = STORTINGET_URI, name = "fra") var from: String? = null,
                   @XmlElement(namespace = STORTINGET_URI, name = "til") var to: String? = null)

@XmlRootElement(namespace = STORTINGET_URI, name = "sporsmal")
@XmlAccessorType(XmlAccessType.FIELD)
data class Question(@XmlElement(namespace = STORTINGET_URI, name = "id") var id: String? = null,
                    @XmlElement(namespace = STORTINGET_URI, name = "besvart_av") var answeredBy: Representative? = null,
                    @XmlElement(namespace = STORTINGET_URI, name = "besvart_av_minister_id") var answeredByMinisterId: String? = null,
                    @XmlElement(namespace = STORTINGET_URI, name = "besvart_av_minister_tittel") var answeredByMinisterTitle: String? = null,
                    @XmlElement(namespace = STORTINGET_URI, name = "besvart_dato") var answeredDate: String? = null,
                    @XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av") var answeredFor: String? = null,
                    @XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av_minister_id") var answeredForMinisterId: String? = null,
                    @XmlElement(namespace = STORTINGET_URI, name = "besvart_pa_vegne_av_minister_tittel") var answeredForMinisterTitle: String? = null,
                    @XmlElement(namespace = STORTINGET_URI, name = "datert_dato") var dated: String? = null,
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
                    @XmlElement(namespace = STORTINGET_URI, name = "type") var type: String? = null)

interface Consumer<in T> {

    fun onElement(element: T)

    object Printing : Consumer<Any> {
        override fun onElement(element: Any) {
            println(element)
        }
    }
}

class ThrottledReader<T>(val endpoint: String, type: Class<out T>) {

    private val unmarshaller = JAXBContext.newInstance(type).createUnmarshaller();

    private val tag = type.getAnnotation(XmlRootElement::class.java).name

    fun read(vararg consumers: Consumer<T>) {
        val stream = URL(EXPORT_URI + endpoint).openStream();
        try {
            val reader = XMLInputFactory.newFactory().createXMLEventReader(stream);
            try {
                while (reader.hasNext()) {
                    val event = reader.peek();
                    if (event != null && event.isStartElement && (event as StartElement).name.localPart == tag) {
                        @Suppress("UNCHECKED_CAST")
                        val element = unmarshaller.unmarshal(reader) as T;
                        consumers.forEach { it.onElement(element) }
                    } else {
                        reader.next();
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
    ThrottledReader("allekomiteer", Committee::class.java).read(Consumer.Printing)
    ThrottledReader("allepartier", Party::class.java).read(Consumer.Printing)
    ThrottledReader("fylker", Area::class.java).read(Consumer.Printing)
    ThrottledReader("stortingsperioder", Period::class.java).read(Consumer.Printing, object : Consumer<Period> {
        override fun onElement(element: Period) {
            ThrottledReader("representanter?stortingsperiodeid=${element.id}", Representative::class.java).read(Consumer.Printing)
        }
    })
    ThrottledReader("emner", Topic::class.java).read(Consumer.Printing)
    ThrottledReader("sesjoner", Session::class.java).read(Consumer.Printing, object : Consumer<Session> {
        override fun onElement(element: Session) {
            ThrottledReader("komiteer?sesjonid=${element.id}", Committee::class.java).read(Consumer.Printing)
            ThrottledReader("partier?sesjonid=${element.id}", Party::class.java).read(Consumer.Printing)
            ThrottledReader("sporretimesporsmal?sesjonid=${element.id}", Question::class.java).read(Consumer.Printing)
        }
    })
}
