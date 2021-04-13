package de.smartmoney.gpeixoto.challenge.withdraw;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import de.smartmoney.gpeixoto.challenge.shared.BusinessException;
import de.smartmoney.gpeixoto.challenge.user.User;
import de.smartmoney.gpeixoto.challenge.user.UserRepository;

@Service
public class WithdrawService {

	private final WithdrawRepository repository;
	private final UserRepository userRepository;
	
	public WithdrawService(WithdrawRepository repository, UserRepository userRepository) {
		this.repository = repository;
		this.userRepository = userRepository;
	}

	public Optional<Withdraw> find(Long code) {
		return repository.findByCode(code);
	}

	@Transactional
	public Withdraw save(Withdraw withdraw) {

		Optional<User> optional;
		
		if(withdraw.getUser().getCode() != null) {
			optional = userRepository.findByCode(withdraw.getUser().getCode());
		}else {
			optional = userRepository.findByEmail(withdraw.getUser().getEmail());
		}
				
		if(optional.isEmpty()) {
			throw new BusinessException("user", "User not found");
		}
		
		User user = optional.get();
		
		withdraw.setUser(user);

		repository.lockByUser(user.getId());

		Long count = repository.countByUser(user);

		String firstMinimum = "50.00";
		if (count == 0 && isValueGreater(withdraw, firstMinimum)) {
			throw new BusinessException("value", "Your first withdraw is limited to $" + firstMinimum);
		}

		if (countLast24Hours(user) >= 5) {
			throw new BusinessException("value", "You have reached the daily withdrawals limit");
		}

		Integer percentage;

		if (isValueEqualOrLess(withdraw, "100.99")) {
			percentage = 3;
		} else if (isValueEqualOrLess(withdraw, "250.99")) {
			percentage = 2;
		} else {
			percentage = 1;
		}
		
		withdraw.setFee(feeValue(withdraw, percentage));
		
		withdraw.setCode(repository.generateNextCode());
		
		// HSQL Database is limited to millisecond precision
		withdraw.setCreatedDate(Instant.now().truncatedTo(ChronoUnit.MILLIS));

		return repository.save(withdraw);
	}
	
	public List<Withdraw> list() {
		return repository.findAll();
	}
	
	private Long countLast24Hours(User user) {
		Instant after = Instant.now().minus(24, ChronoUnit.HOURS);
		return repository.countByUserAndCreatedDateAfter(user, after);
	}
	
	private Boolean isValueGreater(Withdraw withdraw, String value) {
		return withdraw.getValue().compareTo(new BigDecimal(value)) > 0;
	}
	
	private Boolean isValueEqualOrLess(Withdraw withdraw, String value) {
		return withdraw.getValue().compareTo(new BigDecimal(value)) <= 0;
	}
	
	private BigDecimal feeValue(Withdraw withdraw, Integer percentage) {
		return withdraw.getValue().multiply(new BigDecimal(percentage)).divide(new BigDecimal(100));
	}
}