package bloodmatch.domain.party;

import bloodmatch.domain.shared.valueObjects.CPF;

import java.time.LocalDate;

public class Man extends Person {

  public Man(
      String name,
      CPF cpf,
      LocalDate birthDate) {

    super(name, cpf, birthDate);
  }

}