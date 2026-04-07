package pl.sgorski.expense_splitter.features.expense.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.sgorski.expense_splitter.config.CentralMapperConfig;
import pl.sgorski.expense_splitter.features.expense.domain.Expense;
import pl.sgorski.expense_splitter.features.expense.domain.ExpenseShare;
import pl.sgorski.expense_splitter.features.expense.dto.command.CreateExpenseCommand;
import pl.sgorski.expense_splitter.features.expense.dto.command.ParticipantCommand;
import pl.sgorski.expense_splitter.features.expense.dto.command.UpdateExpenseCommand;
import pl.sgorski.expense_splitter.features.expense.dto.request.CreateExpenseRequest;
import pl.sgorski.expense_splitter.features.expense.dto.request.ExpenseParticipantRequest;
import pl.sgorski.expense_splitter.features.expense.dto.request.UpdateExpenseRequest;
import pl.sgorski.expense_splitter.features.expense.dto.response.DetailedExpenseResponse;
import pl.sgorski.expense_splitter.features.expense.dto.response.ExpenseResponse;
import pl.sgorski.expense_splitter.features.expense.dto.response.ExpenseShareResponse;

@Mapper(config = CentralMapperConfig.class)
public interface ExpenseMapper {
  // NOTE: amount and percentage are ignored until other split types are not implemented
  @Mapping(target = "amount", ignore = true)
  @Mapping(target = "percentage", ignore = true)
  ParticipantCommand toParticipantCommand(ExpenseParticipantRequest request);

  @Mapping(target = "splitType", expression = "java(SplitType.EQUAL)")
  CreateExpenseCommand toCreateExpenseCommand(CreateExpenseRequest request);

  UpdateExpenseCommand toUpdateExpenseCommand(UpdateExpenseRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "payer", ignore = true)
  @Mapping(target = "amountTotal", source = "amount")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "shares", ignore = true)
  @Mapping(target = "splitType", ignore = true)
  void updateExpense(UpdateExpenseCommand command, @MappingTarget Expense expense);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "amountTotal", source = "amount")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "shares", ignore = true)
  @Mapping(target = "payer", ignore = true)
  Expense toEntity(CreateExpenseCommand command);

  ExpenseShareResponse toShareResponse(ExpenseShare share);

  ExpenseResponse toResponse(Expense expense);

  DetailedExpenseResponse toDetailedResponse(Expense expense);
}
