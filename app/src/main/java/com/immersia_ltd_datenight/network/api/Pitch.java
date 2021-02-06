package com.immersia_ltd_datenight.network.api;

public class Pitch {
    String _id;
    String email;
    String[] companyCountry;
    String[] businessType;
    String[] corporateStructure;
    int raisingAmount;
    int[] amountReceived;
    String[] investor;
    String firstname;
    String lastname;
    Long phoneNumber; //int-  java.lang.NumberFormatException: Expected an int but was 9172435565 at line 1 column 1498 path $.data[0].phoneNumber
    String raisingType;
    String companyNumber;
    String RegisteredCompanyName;
    String tradingName;
    String addressLine1;
    String addressLine2;
    String city;
    String executiveSummary;
    String milestone;
    String equityOffer;
    String premoneyValuation;
    String idea;
    int amountRaised;
    int sharePrice;
    int daysLeftToInvest;
//    TeamObject teamMember1;
//    TeamObject teamMember2;
//    TeamObject teamMember3;
//    TeamObject teamMember4;
//    TeamObject teamMember5;

    //TeamObject teamMember1, TeamObject teamMember2, TeamObject teamMember3, TeamObject teamMember4, TeamObject teamMember5
    public Pitch(String pitchId, String email, String[] companyCountry, String[] businessType, String[] corporateStructure, int raisingAmount, int[] amountReceived, String[] investor, String firstname, String lastname, Long phoneNumber, String raisingType, String companyNumber, String registeredCompanyName, String tradingName, String addressLine1, String addressLine2, String city, String executiveSummary, String milestone, String equityOffer, String premoneyValuation, String idea, int amountRaised, int sharePrice, int daysLeftToInvest) {
        this._id = pitchId;
        this.email = email;
        this.companyCountry = companyCountry;
        this.businessType = businessType;
        this.corporateStructure = corporateStructure;
        this.raisingAmount = raisingAmount;
        this.amountReceived = amountReceived;
        this.investor = investor;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.raisingType = raisingType;
        this.companyNumber = companyNumber;
        this.RegisteredCompanyName = registeredCompanyName;
        this.tradingName = tradingName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.executiveSummary = executiveSummary;
        this.milestone = milestone;
        this.equityOffer = equityOffer;
        this.premoneyValuation = premoneyValuation;
        this.idea = idea;
        this.amountRaised = amountRaised;
        this.sharePrice = sharePrice;
        this.daysLeftToInvest = daysLeftToInvest;
//        this.teamMember1 = teamMember1;
//        this.teamMember2 = teamMember2;
//        this.teamMember3 = teamMember3;
//        this.teamMember4 = teamMember4;
//        this.teamMember5 = teamMember5;
    }

    //@SerializedName("_id")--Not working
    public String getPitchId() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public String[] getCompanyCountry() {
        return companyCountry;
    }

    public String[] getBusinessType() {
        return businessType;
    }

    public String[] getCorporateStructure() {
        return corporateStructure;
    }

    public int getRaisingAmount() {
        return raisingAmount;
    }

    public int[] getAmountReceived() {
        return amountReceived;
    }

    public String[] getInvestor() {
        return investor;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public String getRaisingType() {
        return raisingType;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getRegisteredCompanyName() {
        return RegisteredCompanyName;
    }

    public String getTradingName() {
        return tradingName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getExecutiveSummary() {
        return executiveSummary;
    }

    public String getMilestone() {
        return milestone;
    }

    public String getEquityOffer() {
        return equityOffer;
    }

    public String getPremoneyValuation() {
        return premoneyValuation;
    }

    public String getIdea() {
        return idea;
    }

    public int getAmountRaised() {
        return amountRaised;
    }

    public int getSharePrice() {
        return sharePrice;
    }

    public int getDaysLeftToInvest() {
        return daysLeftToInvest;
    }

//    public TeamObject getTeamMember1() {
//        return teamMember1;
//    }
//
//    public TeamObject getTeamMember2() {
//        return teamMember2;
//    }
//
//    public TeamObject getTeamMember3() {
//        return teamMember3;
//    }
//
//    public TeamObject getTeamMember4() {
//        return teamMember4;
//    }
//
//    public TeamObject getTeamMember5() {
//        return teamMember5;
//    }
}
