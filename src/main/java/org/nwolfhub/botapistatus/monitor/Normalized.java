package org.nwolfhub.botapistatus.monitor;

import java.io.Serializable;

public class Normalized implements Serializable {
    public Long normalized;
    public Long amount;

    public Normalized() {}

    public Long getNormalized() {
        return normalized;
    }

    public Normalized setNormalized(Long normalized) {
        this.normalized = normalized;
        return this;
    }

    public Long getAmount() {
        return amount;
    }

    public Normalized setAmount(Long amount) {
        this.amount = amount;
        return this;
    }

    public void addValue(Long value) {
        if(normalized!=null) {
            if(Math.abs(normalized-value)>250) {
                if(amount<100) { //ignore too high responses. This is a normal value of response time for server and not medium one
                    this.normalized = (normalized*amount+value)/(amount+1);
                    amount++;
                }
            } else {
                this.normalized = (normalized*amount+value)/(amount+1);
                amount++;
            }
        }

    }
}
