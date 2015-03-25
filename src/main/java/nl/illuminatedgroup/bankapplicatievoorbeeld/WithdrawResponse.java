package nl.illuminatedgroup.bankapplicatievoorbeeld;

import org.codehaus.jackson.annotate.JsonProperty;

public class WithdrawResponse 
{
    @JsonProperty
    private String response;
    @JsonProperty
    private long transactieNummer;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public long getTransactieNummer() {
        return transactieNummer;
    }

    public void setTransactieNummer(long transactieNummer) {
        this.transactieNummer = transactieNummer;
    }
}
