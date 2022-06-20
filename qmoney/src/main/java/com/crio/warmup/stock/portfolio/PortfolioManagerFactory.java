package com.crio.warmup.stock.portfolio;

import org.springframework.web.client.RestTemplate;

public class PortfolioManagerFactory {
    public static PortfolioManager getPortfolioManager(RestTemplate restTemplate) {
        PortfolioManager portfolioManager = new PortfolioManagerImpl(restTemplate);
        return portfolioManager;
    }
}
