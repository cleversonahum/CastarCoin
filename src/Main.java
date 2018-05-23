import blockchain.*;
import transaction.*;
import wallet.Wallet;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		Blockchain blockchain = new Blockchain();
		TxPool txPool = new TxPool();
		Wallet wallet = new Wallet();

		System.out.println("So far so good");

		// Needs real keys
		// To be removed
		/*wallet.createTransaction(new PublicKey() {
									@Override
									public String getAlgorithm() {
										return null;
									}

									@Override
									public String getFormat() {
										return null;
									}

									@Override
									public byte[] getEncoded() {
										return new byte[0];
									}
								},
								10,
								new PrivateKey() {
									@Override
									public String getAlgorithm() {
										return null;
									}

									@Override
									public String getFormat() {
										return null;
									}

									@Override
									public byte[] getEncoded() {
										return new byte[0];
									}
								},
								new ArrayList<UnspentTxOut>(),
								new ArrayList<Transaction>()
		);*/
	}
}
