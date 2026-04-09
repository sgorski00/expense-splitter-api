package pl.sgorski.expense_splitter.features.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sgorski.expense_splitter.config.CentralMapperConfig;
import pl.sgorski.expense_splitter.features.expense.dto.filter.ExpenseRole;
import pl.sgorski.expense_splitter.features.expense.mapper.ExpenseMapper;
import pl.sgorski.expense_splitter.features.payment.domain.Payment;
import pl.sgorski.expense_splitter.features.payment.dto.command.CreatePaymentCommand;
import pl.sgorski.expense_splitter.features.payment.dto.request.CreatePaymentRequest;
import pl.sgorski.expense_splitter.features.payment.dto.response.PaymentResponse;

@Mapper(config = CentralMapperConfig.class)
public abstract class PaymentMapper {
  @Autowired protected ExpenseMapper expenseMapper;

  @Mapping(
      target = "expense",
      expression = "java(expenseMapper.toResponse(payment.getExpense(), role))")
  public abstract PaymentResponse toResponse(Payment payment, ExpenseRole role);

  public abstract CreatePaymentCommand toCreateCommand(CreatePaymentRequest request);
}
