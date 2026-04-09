package bloodmatch.domain.party;

import bloodmatch.domain.shared.valueObjects.CPF;

import java.time.LocalDate;

public class Woman extends Person {

  public Woman(
      String name,
      CPF cpf,
      LocalDate birthDate) {

    super(name, cpf, birthDate);
  }

}